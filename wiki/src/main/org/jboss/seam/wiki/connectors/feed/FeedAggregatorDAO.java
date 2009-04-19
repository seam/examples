/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;

import java.util.*;
import java.io.Serializable;
import java.net.URL;

/**
 * Calls feed connector several times for several feed URLs, optionally stores the result in a cache.
 *
 * <p>
 * Reads the feed information from the feed connector (or the transparent cache on top of the connector) and
 * then aggregates the data into <tt>FeedEntryDTO</tt> instances. These instances are then again cached
 * in the <tt>FeedAggregateCache</tt>. We basically have a DTO/DAO/Cache layer on top of the connector layer (with
 * its own caching). However, this aggregate cache is not transparent, so if a client wants to get cached
 * <tt>FeedEntryDTO</tt> objects, it needs to ask the <tt>FeedAggregateCache</tt> directly, not this DAO. The DAO
 * just puts stuff <i>into</i> the cache when its loaded from the connector.
 * </p>
 * <p>
 * The primary motivation behind this architecture is resolving the disconnect that exists between reading external
 * feeds and storing them in-memory for further reading (display on pages, exposing aggregated feeds). We also need
 * to channel parameters, that is, a page might want to render external feeds A and B. However, the connector layer
 * can only handle a single feed at a time, so the additional aggregation layer was added to combine data from
 * several feeds (with potentially several connector calls). 
 * </p>
 * <p>
 * Finally, caching the aggregated feeds is optional. If a client asks this DAO to aggregate feed entries of
 * several external feeds, and does not supply a cache key (the aggregateId), the result is not cached in the
 * <tt>FeedAggregateCache</tt>. So <i>other</i> clients (which might also only know the aggregateId, not the
 * external feed URLs) can not access the data on the <tt>FeedAggregateCache</tt>.
 * </p>
 *
 * @author Christian Bauer
 */
@Name("feedAggregatorDAO")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class FeedAggregatorDAO implements Serializable {

    @Logger
    Log log;

    @In("feedConnectorCache")
    FeedConnector feedConnector;

    @In
    FeedAggregateCache feedAggregateCache;

    public List<FeedEntryDTO> getLatestFeedEntries(int numberOfFeedEntries, URL[] feedURLs) {
        return getLatestFeedEntries(numberOfFeedEntries, feedURLs, null);
    }

    public List<FeedEntryDTO> getLatestFeedEntries(int numberOfFeedEntries, URL[] feedURLs, String aggregateId) {
        if (feedURLs == null) return Collections.EMPTY_LIST;

        List<FeedEntryDTO> feedEntries = new ArrayList<FeedEntryDTO>();

        for (URL feedURL : feedURLs) {
            try {
                // For each feed, get the feed entries and put them in a sorted collection,
                // so we get overall sorting
                log.debug("retrieving feed entries from connector for feed URL: " + feedURL);

                // TODO: This is a synchronized call. This probably means that this is a bottleneck for scalability because
                // the Seam locking is very coarse-grained. It would be much better if we could aquire exclusive read/write
                // locks, not just exclusive locks.
                List<FeedEntryDTO> result = feedConnector.getFeedEntries(feedURL.toString());

                log.debug("retrieved feed entries: " + result.size());
                feedEntries.addAll(result);
                log.debug("number of aggregated feed entries so far: " + feedEntries.size());

            } catch (IllegalStateException ex) {
                // This is most likely (we hope) a message that says that an exlusive read lock couldn't be aquired.
                // Too bad, we just continue without adding the result... the next user requesting it will probably 
                // get the lock and then we have the result.
                log.warn("Illegal state exception thrown by feed connector: " + ex.getMessage());
            }
        }

        Collections.sort(
            feedEntries,
            // Sort by date of feed entry ascending
            new Comparator<FeedEntryDTO>() {
                public int compare(FeedEntryDTO a, FeedEntryDTO b) {
                    if (a.getFeedEntry().getUpdatedDate() != null && b.getFeedEntry().getUpdatedDate() != null) {
                        if (a.getFeedEntry().getUpdatedDate().getTime() >
                            b.getFeedEntry().getUpdatedDate().getTime()) return -1;

                        return (a.getFeedEntry().getUpdatedDate().getTime() ==
                                b.getFeedEntry().getUpdatedDate().getTime() ? 0 : 1);

                    } else {
                        if (a.getFeedEntry().getPublishedDate().getTime() >
                            b.getFeedEntry().getPublishedDate().getTime()) return -1;

                        return (a.getFeedEntry().getPublishedDate().getTime() ==
                                b.getFeedEntry().getPublishedDate().getTime() ? 0 : 1);
                    }
                }
            }
        );

        if (aggregateId != null) {
            log.debug("caching aggregated feed entries under id: " + aggregateId);
            // Cache the result for later requests through FeedServlet (by aggregateId)
            feedAggregateCache.put(aggregateId, feedEntries);
        }

        return feedEntries.size() > numberOfFeedEntries
                ? new ArrayList<FeedEntryDTO>(feedEntries).subList(0, numberOfFeedEntries)
                : new ArrayList<FeedEntryDTO>(feedEntries);
    }
}
