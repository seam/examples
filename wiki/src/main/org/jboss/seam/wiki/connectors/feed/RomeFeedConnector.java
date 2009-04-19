/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.core.model.FeedEntry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sun.net.www.protocol.http.HttpURLConnection;

/**
 *
 * TODO: This requires a system property for timeout: sun.net.client.defaultConnectTimeout=30000
 * 
 * @author Christian Bauer
 */
@Name("feedConnector")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class RomeFeedConnector implements FeedConnector {

    @Logger
    Log log;

    @In("#{preferences.get('FeedConnector')}")
    FeedConnectorPreferences prefs;

    public List<FeedEntryDTO> getFeedEntries(String feedURL) {

        try {

            List<FeedEntryDTO> feedEntryDTOs = new ArrayList<FeedEntryDTO>();

            log.debug("connecting to feed URL: " + feedURL);

            URL feedSource = new URL(feedURL);

            /** TODO: This breaks multi-threading somehow, two threads start calling this connector and one never returns...
            URLConnection connection = feedSource.openConnection();
            connection.setConnectTimeout(prefs.getConnectionTimeoutSeconds().intValue()*1000);
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed syndFeed = input.build(new XmlReader(connection));
             */
            // So we let Rome do it, whatever it uses internally seems to be safer...
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed syndFeed = input.build(new XmlReader(feedSource));

            log.debug("starting conversion from feed URL: " + feedURL);

            Feed feed = convertSyndFeed(syndFeed);

            for (Object o : syndFeed.getEntries()) {
                SyndEntry syndEntry = (SyndEntry)o;
                FeedEntry fe = convertSyndEntry(syndEntry);

                // Append some information on the title
                fe.setTitlePrefix("(" + feed.getTitle() + ") ");
                //fe.setTitleSuffix(" (" + fe.getAuthor() + ")");

                // Linking it in our model makes it persistable/cachable
                feed.getFeedEntries().add(fe);

                // Now project them so the client has a unified view without iterating collections of Feeds
                FeedEntryDTO dto = new FeedEntryDTO(feed, fe);
                feedEntryDTOs.add(dto);
            }

            log.debug("retrieved feed entries: " + feedEntryDTOs.size());
            return feedEntryDTOs;

        } catch (SocketTimeoutException timeoutEx) {
            log.warn("timeout connecting to feed: " + feedURL + ", " + timeoutEx.getMessage());
        } catch (IllegalArgumentException iaEx) {
            log.warn("could not connect to feed: " + feedURL + ", " + iaEx.getMessage());
        } catch (MalformedURLException urlEx) {
            log.warn("URL is not valid: " + feedURL + ", " + urlEx.getMessage());
        } catch (IOException ioEx) {
            log.warn("could not connect to feed: " + feedURL + ", " + ioEx.getMessage());
        } catch (FeedException fex) {
            log.warn("could not marshall feed data: " + feedURL + ", " + fex.getMessage());
        }
        return Collections.EMPTY_LIST;
    }

    private Feed convertSyndFeed(SyndFeed syndFeed) {
        log.debug("converting SyndFeed: " + syndFeed.getTitle());
        Feed feed = new Feed();
        feed.setLink(syndFeed.getLink());
        feed.setTitle(syndFeed.getTitle());
        feed.setPublishedDate(syndFeed.getPublishedDate());
        feed.setDescription(syndFeed.getDescription());
        feed.setAuthor(syndFeed.getAuthor());
        return feed;
    }

    private FeedEntry convertSyndEntry(SyndEntry syndEntry) {
        log.debug("converting SyndEntry: " + syndEntry.getTitle());
        FeedEntry feedEntry = new FeedEntry();
        feedEntry.setLink(syndEntry.getLink());
        feedEntry.setTitle(syndEntry.getTitle());
        feedEntry.setPublishedDate(syndEntry.getPublishedDate());
        feedEntry.setUpdatedDate(syndEntry.getUpdatedDate());
        feedEntry.setAuthor(syndEntry.getAuthor());

        if (syndEntry.getDescription() != null) {
            SyndContent description = syndEntry.getDescription();

            // TODO: Hardcode 'html', otherwise the ROME stuff craps out and kills Firefox feed renderer...
            feedEntry.setDescriptionType("html");
            feedEntry.setDescriptionValue(description.getValue());
        }

        return feedEntry;
    }

}
