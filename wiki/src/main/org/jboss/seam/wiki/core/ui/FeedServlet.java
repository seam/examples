/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import com.sun.syndication.io.FeedException;
import org.jboss.seam.Component;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.Messages;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.Authenticator;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.dao.WikiNodeFactory;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.util.Hash;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.connectors.feed.FeedAggregateCache;
import org.jboss.seam.wiki.connectors.feed.FeedEntryDTO;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Serves syndicated feeds, one feed for each directory that has a feed.
 * <p>
 * This servlet uses either the currently logged in user (session) or
 * basic HTTP authorization if there is no user logged in or if the feed
 * requires a higher access level than currently available. Feed entries are also
 * read-access filtered. Optionally, requests can enable/disable comments on the feed
 * or filter by tag. It's up to the actual <tt>WikiFeedEntry</tt> instance how these
 * filters are applied.
 * </p>
 *
 * @author Christian Bauer
 */
public class FeedServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(FeedServlet.class);

    public static enum Comments {
        include, exclude, only
    }

    // Possible feed types
    public enum SyndFeedType {
        ATOM("/atom.seam", "atom_1.0", "application/atom+xml");
        // TODO: I don't think we'll ever do that: ,RSS2("/rss.seam", "rss_2.0", "application/rss+xml");

        SyndFeedType(String pathInfo, String feedType, String contentType) {
            this.pathInfo = pathInfo;
            this.feedType = feedType;
            this.contentType = contentType;
        }
        String pathInfo;
        String feedType;
        String contentType;
    }

    // Supported feed types
    private Map<String, SyndFeedType> feedTypes = new HashMap<String,SyndFeedType>() {{
        put(SyndFeedType.ATOM.pathInfo, SyndFeedType.ATOM);
    }};

    // Allow unit testing
    public FeedServlet() {}

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        new ContextualHttpServletRequest(request) {
            @Override
            public void process() throws Exception {
                doWork(request, response);
            }
        }.run();
    }

    // TODO: All data access in this method runs with auto-commit mode, see http://jira.jboss.com/jira/browse/JBSEAM-957
    protected void doWork(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String feedIdParam = request.getParameter("feedId");
        String areaNameParam = request.getParameter("areaName");
        String nodeNameParam = request.getParameter("nodeName");
        String aggregateParam = request.getParameter("aggregate");
        log.debug(">>> feed request id: '" + feedIdParam + "' area name: '" + areaNameParam + "' node name: '" + nodeNameParam + "'");

        Contexts.getSessionContext().set("LAST_ACCESS_ACTION", "Feed: " +feedIdParam + " area: '" + areaNameParam + "' node: '" + nodeNameParam + "'");

        // Feed type
        String pathInfo = request.getPathInfo();
        log.debug("requested feed type: " + pathInfo);
        if (!feedTypes.containsKey(pathInfo)) {
            log.debug("can not render this feed type, returning BAD REQUEST");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported feed type " + pathInfo);
            return;
        }
        SyndFeedType syndFeedType = feedTypes.get(pathInfo);

        // Comments
        String commentsParam = request.getParameter("comments");
        Comments comments  = Comments.include;
        if (commentsParam != null && commentsParam.length() >0) {
            try {
                comments = Comments.valueOf(commentsParam);
            } catch (IllegalArgumentException ex) {
                log.info("invalid comments request parameter: " + commentsParam);
            }
        }
        log.debug("feed rendering handles comments: " + comments);

        // Tag
        String tagParam = request.getParameter("tag");
        String tag = null;
        if (tagParam != null && tagParam.length() >0) {
            log.debug("feed rendering restricts on tag: " + tagParam);
            tag = tagParam;
        }

        Feed feed = resolveFeed(aggregateParam, feedIdParam, areaNameParam, nodeNameParam);

        if (feed == null) {
            log.debug("feed not found, returning NOT FOUND");
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feed");
            return;
        }

        log.debug("checking permissions of " + feed);
        // Authenticate and authorize, first with current user (session) then with basic HTTP authentication
        Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
        if (feed.getReadAccessLevel() > currentAccessLevel) {
            boolean loggedIn = ((Authenticator)Component.getInstance(Authenticator.class)).authenticateBasicHttp(request);
            currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
            if (!loggedIn || feed.getReadAccessLevel() > currentAccessLevel) {
                log.debug("requiring authentication, feed has higher access level than current");
                response.setHeader("WWW-Authenticate", "Basic realm=\"" + feed.getTitle().replace("\"", "'") + "\"");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        Date lastFeedEntryDate = null;
        if (feed.getId() != null) {

            // Ask the database what the latest feed entry is for that feed, then use its updated timestamp hash
            FeedDAO feedDAO = (FeedDAO)Component.getInstance(FeedDAO.class);
            List<FeedEntry> result = feedDAO.findLastFeedEntries(feed.getId(), 1);
            if (result.size() > 0) {
                lastFeedEntryDate = result.get(0).getUpdatedDate();
            }

        } else {

            // Get the first (latest) entry of the aggregated feed and use its published timestamp hash (ignoring updates!)
            // There is a wrinkle hidden here: What if a feed entry is updated? Then the published timestamp should also
            // be different because the "first latest" feed entry in the list is sorted by both published and updated
            // timestamps. So even though we only use published timestamp hash as an ETag, this timestamp also changes
            // when a feed entry is updated because the collection order changes as well.
            if (feed.getFeedEntries().size() > 0) {
                lastFeedEntryDate = feed.getFeedEntries().iterator().next().getPublishedDate();
            }
        }
        if (lastFeedEntryDate != null) {
            String etag = calculateEtag(lastFeedEntryDate);
            log.debug("setting etag header: " + etag);
            response.setHeader("ETag", etag);
            String previousToken = request.getHeader("If-None-Match");
            if (previousToken != null && previousToken.equals(etag)) {
                log.debug("found matching etag in request header, returning 304 Not Modified");
                response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
        }

        // TODO: Refactor this parameter mess a little
        log.debug("finally rendering feed");
        SyndFeed syndFeed =
                createSyndFeed(
                    request.getRequestURL().toString(),
                    syndFeedType,
                    feed,
                    currentAccessLevel,
                    tag,
                    comments,
                    aggregateParam
                );

        // If we have an entry on this feed, take the last entry's update timestamp and use it as
        // the published timestamp of the feed. The Rome library does not have a setUpdatedDate()
        // method and abuses the published date to write <updated> into the Atom <feed> element.
        if (lastFeedEntryDate != null) {
            syndFeed.setPublishedDate(lastFeedEntryDate);
        }

        // Write feed to output
        response.setContentType(syndFeedType.contentType);
        response.setCharacterEncoding("UTF-8");
        SyndFeedOutput output = new SyndFeedOutput();
        try {
            output.output(syndFeed, response.getWriter());
        } catch (FeedException ex) {
            throw new ServletException(ex);
        }
        response.getWriter().flush();

        log.debug("<<< feed rendering complete");
    }

    public Feed resolveFeed(String aggregateParam, String feedIdParam, String areaNameParam, String nodeNameParam) {
        Feed feed;
        // Find the feed, depending on variations of request parameters
        if (aggregateParam != null && aggregateParam.length() > 0) {
            feed = resolveFeedWithAggregateId(aggregateParam);
        } else if (feedIdParam != null && feedIdParam.length() >0) {
            feed = resolveFeedWithFeedId(feedIdParam);
        } else if (areaNameParam != null && areaNameParam.length() > 0) {
            feed = resolveFeedWithAreaNameAndNodeName(areaNameParam, nodeNameParam);
        } else {
            log.debug("no aggregate id, no feed id, no area name requested, getting wikiRoot feed");
            WikiNodeFactory factory = (WikiNodeFactory)Component.getInstance(WikiNodeFactory.class);
            feed = factory.loadWikiRoot().getFeed();
        }
        return feed;
    }

    public Feed resolveFeedWithAggregateId(String aggregateId) {
        Feed feed = null;
        log.debug("trying to retrieve aggregated feed from cache: " + aggregateId);
        FeedAggregateCache aggregateCache = (FeedAggregateCache)Component.getInstance(FeedAggregateCache.class);
        List<FeedEntryDTO> result = aggregateCache.get(aggregateId);
        if (result != null) {
            feed = new Feed();
            feed.setAuthor(Messages.instance().get("lacewiki.msg.AutomaticallyGeneratedFeed"));
            feed.setTitle(Messages.instance().get("lacewiki.msg.AutomaticallyGeneratedFeed") + ": " + aggregateId);
            feed.setPublishedDate(new Date());
            // We are lying here, we don't really have an alternate representation link for this resource
            feed.setLink( Preferences.instance().get(WikiPreferences.class).getBaseUrl() );
            for (FeedEntryDTO feedEntryDTO : result) {
                feed.getFeedEntries().add(feedEntryDTO.getFeedEntry());
            }
        }
        return feed;
    }

    public Feed resolveFeedWithFeedId(String feedId) {
        Feed feed = null;
        try {
            log.debug("trying to retrieve feed for id: " + feedId);
            Long feedIdentifier = Long.valueOf(feedId);
            FeedDAO feedDAO = (FeedDAO)Component.getInstance(FeedDAO.class);
            feed = feedDAO.findFeed(feedIdentifier);
        } catch (NumberFormatException ex) {
            log.debug("feed identifier couldn't be converted to java.lang.Long");
        }
        return feed;
    }

    public Feed resolveFeedWithAreaNameAndNodeName(String areaName, String nodeName) {
        Feed feed = null;
        if (!areaName.matches("^[A-Z0-9]+.*")) return feed;
        log.debug("trying to retrieve area: " + areaName);
        WikiNodeDAO nodeDAO = (WikiNodeDAO)Component.getInstance(WikiNodeDAO.class);
        WikiDirectory area = nodeDAO.findAreaUnrestricted(areaName);
        if (area != null && (nodeName == null || !nodeName.matches("^[A-Z0-9]+.*")) && area.getFeed() != null) {
            log.debug("using feed of area, no node requested: " + area);
            feed = area.getFeed();
        } else if (area != null && nodeName != null && nodeName.matches("^[A-Z0-9]+.*")) {
            log.debug("trying to retrieve node: " + nodeName);
            WikiDirectory nodeDir = nodeDAO.findWikiDirectoryInAreaUnrestricted(area.getAreaNumber(), nodeName);
            if (nodeDir != null && nodeDir.getFeed() != null) {
                log.debug("using feed of node: " + nodeDir);
                feed = nodeDir.getFeed();
            } else {
                log.debug("node not found or node has no feed");
            }
        } else {
            log.debug("area not found or area has no feed");
        }
        return feed;
    }

    public SyndFeed createSyndFeed(String baseURI, SyndFeedType syndFeedType, Feed feed, Integer currentAccessLevel) {
        return createSyndFeed(baseURI, syndFeedType, feed, currentAccessLevel, null, Comments.include, null);
    }

    public SyndFeed createSyndFeed(String baseURI,
                                   SyndFeedType syndFeedType,
                                   Feed feed,
                                   Integer currentAccessLevel,
                                   String tag,
                                   Comments comments,
                                   String aggregateParam) {

        WikiPreferences prefs = Preferences.instance().get(WikiPreferences.class);

        // Create feed
        SyndFeed syndFeed = new SyndFeedImpl();
        String feedUri =
                feed.getId() != null
                    ? "?feedId="+feed.getId()
                    : "?aggregate="+WikiUtil.encodeURL(aggregateParam);
        syndFeed.setUri(baseURI + feedUri);
        syndFeed.setFeedType(syndFeedType.feedType);
        syndFeed.setTitle(prefs.getFeedTitlePrefix() + feed.getTitle());
        if (tag != null) {
            syndFeed.setTitle(
                syndFeed.getTitle() + " - " + Messages.instance().get("lacewiki.label.tagDisplay.Tag") + " '" + tag + "'"
            );
        }
        syndFeed.setLink(feed.getLink());
        syndFeed.setAuthor(feed.getAuthor());
        if (feed.getDescription() != null && feed.getDescription().length() >0)
            syndFeed.setDescription(feed.getDescription());

        // Setting the date on which the local feed was stored in the database, might be overwritten later
        syndFeed.setPublishedDate(feed.getPublishedDate());

        // Create feed entries
        List<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
        SortedSet<FeedEntry> entries = feed.getFeedEntries();
        for (FeedEntry entry : entries) {

            if (entry.getReadAccessLevel() > currentAccessLevel) continue;

            if (tag != null && !entry.isTagged(tag)) continue;

            if (comments.equals(Comments.exclude) && entry.isInstance(WikiCommentFeedEntry.class)) continue;
            if (comments.equals(Comments.only) && !entry.isInstance(WikiCommentFeedEntry.class)) continue;

            SyndEntry syndEntry;
            syndEntry = new SyndEntryImpl();
            syndEntry.setTitle(entry.getTitlePrefix() + entry.getTitle() + entry.getTitleSuffix());
            syndEntry.setLink(entry.getLink());
            syndEntry.setUri(entry.getLink());
            syndEntry.setAuthor(entry.getAuthor());
            syndEntry.setPublishedDate(entry.getPublishedDate());
            syndEntry.setUpdatedDate(entry.getUpdatedDate());

            SyndContent description;
            description = new SyndContentImpl();
            description.setType(entry.getDescriptionType());
            description.setValue(WikiUtil.removeMacros(entry.getDescriptionValue()));
            syndEntry.setDescription(description);

            syndEntries.add(syndEntry);
        }
        syndFeed.setEntries(syndEntries);

        return syndFeed;
    }

    private String calculateEtag(Date date) {
        Hash hash = new Hash();
        return hash.hash(date.toString());
    }
}
