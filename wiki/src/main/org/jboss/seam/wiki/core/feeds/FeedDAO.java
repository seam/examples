/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.feeds;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.ui.WikiURLRenderer;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Iterator;

/**
 * DAO for feeds.
 * <p>
 * Uses the <tt>restrictedEntityManager</tt> because it is used in the context of
 * directory/document/comment editing (same PC).
 * </p>
 * <p>
 *
 * @author Christian Bauer
 *
 */
@Name("feedDAO")
@AutoCreate
public class FeedDAO {

    @Logger static Log log;

    @In protected EntityManager restrictedEntityManager;

    /* ############################# FINDERS ################################ */

    public List<Feed> findAllFeeds() {
        return restrictedEntityManager
                .createQuery("select f from Feed f")
                .setHint("org.hibernate.cacheable", true)
                .getResultList();

    }

    public List<WikiFeed> findWikiFeeds() {
        return restrictedEntityManager
                .createQuery("select f from WikiFeed f join fetch f.directory d order by d.createdOn asc")
                .setHint("org.hibernate.cacheable", true)
                .getResultList();

    }

    public Feed findFeed(Long feedId) {
        try {
            return (Feed) restrictedEntityManager
                .createQuery("select f from Feed f where f.id = :id")
                .setParameter("id", feedId)
                .setHint("org.hibernate.cacheable", true)
                .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public List<WikiFeed> findFeeds(WikiDocument document) {
        if (document == null || document.getId() == null) throw new IllegalArgumentException("document is null or unsaved");
        return restrictedEntityManager
                .createQuery(
                    "select distinct f from WikiDocumentFeedEntry fe, WikiFeed f join f.feedEntries allFe " +
                    " where fe.document = :doc and fe = allFe order by f.publishedDate desc"
                )
                .setParameter("doc", document)
                .getResultList();
    }

    public List<WikiFeed> findFeeds(WikiComment comment) {
        if (comment == null || comment.getId() == null) throw new IllegalArgumentException("comment is null or unsaved");
        return restrictedEntityManager
                .createQuery(
                    "select distinct f from WikiCommentFeedEntry fe, WikiFeed f join f.feedEntries allFe " +
                    " where fe.comment = :comment and fe = allFe order by f.publishedDate desc"
                )
                .setParameter("comment", comment)
                .getResultList();
    }

    public List<WikiFeed> findParentFeeds(WikiDirectory startDir, boolean includeSiteFeed) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select f from WikiDirectory d join d.feed f ");
        queryString.append("where d.nodeInfo.nsThread = :nsThread and ");
        queryString.append("d.nodeInfo.nsLeft <= :nsLeft and d.nodeInfo.nsRight >= :nsRight ");
        if (!includeSiteFeed) queryString.append("and not d = :wikiRoot ");
        queryString.append("order by f.publishedDate desc ");

        Query query = restrictedEntityManager.createQuery(queryString.toString())
                .setParameter("nsThread", startDir.getNodeInfo().getNsThread())
                .setParameter("nsLeft", startDir.getNodeInfo().getNsLeft())
                .setParameter("nsRight", startDir.getNodeInfo().getNsRight());

        if (!includeSiteFeed)
            query.setParameter("wikiRoot", Component.getInstance("wikiRoot"));

        return query.getResultList();
    }

    public WikiDocumentFeedEntry findFeedEntry(WikiDocument document) {
        try {
            return (WikiDocumentFeedEntry)restrictedEntityManager
                .createQuery("select fe from WikiDocumentFeedEntry fe where fe.document = :document")
                .setParameter("document", document)
                .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public WikiCommentFeedEntry findFeedEntry(WikiComment comment) {
        try {
            return (WikiCommentFeedEntry)restrictedEntityManager
                .createQuery("select fe from WikiCommentFeedEntry fe where fe.comment = :comment")
                .setParameter("comment", comment)
                .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {}
        return null;
    }

    public List<FeedEntry> findLastFeedEntries(Long feedId, int maxResults) {
        return (List<FeedEntry>) restrictedEntityManager
                .createQuery("select fe from Feed f join f.feedEntries fe where f.id = :feedId order by fe.updatedDate desc")
                .setParameter("feedId", feedId)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public boolean isOnSiteFeed(WikiDocument document) {
        if (document == null || document.getId() == null) throw new IllegalArgumentException("document is null or unsaved");
        Long count = (Long)restrictedEntityManager
                .createQuery("select count(fe) from WikiDocumentFeedEntry fe, WikiFeed f join f.feedEntries allFe " +
                            " where f = :feed and fe.document = :doc and fe = allFe")
                .setParameter("feed", ((WikiDirectory)Component.getInstance("wikiRoot")).getFeed() )
                .setParameter("doc", document)
                .setHint("org.hibernate.cacheable", true)
                .getSingleResult();
        return count != 0;
    }

    /* ############################# FEED CUD ################################ */

    public void createFeed(WikiDirectory dir) {
        WikiURLRenderer urlRenderer = (WikiURLRenderer)Component.getInstance(WikiURLRenderer.class);
        WikiFeed feed = new WikiFeed();
        feed.setDirectory(dir);
        feed.setLink(urlRenderer.renderURL(dir));
        feed.setAuthor(dir.getCreatedBy().getFullname());
        feed.setTitle(dir.getName());
        feed.setDescription(dir.getDescription());
        dir.setFeed(feed);
    }

    public void updateFeed(WikiDirectory dir) {
        WikiURLRenderer urlRenderer = (WikiURLRenderer)Component.getInstance(WikiURLRenderer.class);
        dir.getFeed().setLink(urlRenderer.renderURL(dir));
        dir.getFeed().setTitle(dir.getName());
        dir.getFeed().setAuthor(dir.getCreatedBy().getFullname());
        dir.getFeed().setDescription(dir.getDescription());
    }

    public void removeFeed(WikiDirectory dir) {
        restrictedEntityManager.remove(dir.getFeed());
        dir.setFeed(null);
    }

    /* ############################# FEEDENTRY CUD ################################ */


    public void createFeedEntry(WikiDirectory parentDir, WikiNode node, FeedEntry feedEntry, boolean pushOnSiteFeed) {

        List<WikiFeed> feeds = findParentFeeds(parentDir, pushOnSiteFeed);

        // Now create a feedentry and link it to all the feeds
        if (feeds.size() >0) {
            log.debug("persisting new feed entry for: " + node);
            restrictedEntityManager.persist(feedEntry);
            for (Feed feed : feeds) {
                log.debug("linking new feed entry with feed: " + feed.getId());
                feed.getFeedEntries().add(feedEntry);
            }
        } else {
            log.debug("no available feeds found");
        }
    }

    public void updateFeedEntry(WikiDirectory parentDir, WikiNode node, FeedEntry feedEntry, boolean pushOnSiteFeed) {
        log.debug("updating feed entry: " + feedEntry.getId());

        feedEntry.setUpdatedDate(new Date());

        // Link feed entry with all feeds (there might be new feeds since this feed entry was created)
        List<WikiFeed> feeds = findParentFeeds(parentDir, pushOnSiteFeed);
        for (Feed feed : feeds) {
            log.debug("linking feed entry with feed: " + feed.getId());
            feed.getFeedEntries().add(feedEntry);
        }
    }

    public void removeFeedEntry(List<WikiFeed> feeds, FeedEntry feedEntry) {
        if (feedEntry == null) return;
        // Unlink feed entry from all feeds
        for (Feed feed : feeds) {
            log.debug("remove feed entry from feed: " + feed);
            feed.getFeedEntries().remove(feedEntry);
        }
        log.debug("deleting feed entry");
        restrictedEntityManager.remove(feedEntry);
    }

    public void purgeOldFeedEntries(Date olderThan) {
        log.debug("cleaning up feed entries older than: " + olderThan);
        // Clean up _all_ feed entries that are older than N days
        List<Feed> feedsWithOutdatedEntries =
                restrictedEntityManager
                    .createQuery("select distinct f from FeedEntry fe, WikiFeed f join f.feedEntries allFe " +
                                 " where fe = allFe and fe.publishedDate < :oldestDate")
                    .setParameter("oldestDate", olderThan)
                    .getResultList();
        for (Feed feed : feedsWithOutdatedEntries) {
            log.debug("feed has outdated entries: " + feed);
            Iterator<FeedEntry> it = feed.getFeedEntries().iterator();
            while (it.hasNext()) {
                FeedEntry feedEntry = it.next();
                if (feedEntry.getPublishedDate().compareTo(olderThan) < 0) {
                    log.debug("removing outdated feed entry: " + feedEntry);
                    it.remove(); // Unlink from feed
                    restrictedEntityManager.remove(feedEntry);
                }
            }
        }
    }


}
