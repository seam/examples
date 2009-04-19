/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.wiki.connectors.cache.ConnectorCache;
import org.jboss.seam.wiki.connectors.cache.ConnectorCacheKey;

import java.io.Serializable;
import java.util.List;

/**
 * Caches transient feeds.
 *
 * TODO: Maybe we should use a simple synchronized collection instead, but Seam does the job.
 *
 * @author Christian Bauer
 */
@Name("feedAggregateCache")
@Scope(ScopeType.APPLICATION)
@Synchronized
@AutoCreate
public class FeedAggregateCache extends ConnectorCache<FeedEntryDTO, FeedAggregateCache.FeedAggregateCacheKey> {

    // The stuff in here is valid for 10 minutes
    public static long CACHE_IDLE_TIMEOUT_SECONDS = 36000l;

    public void put(String aggregateId, List<FeedEntryDTO> feedEntries) {
        long currentTime = System.currentTimeMillis();
        FeedAggregateCacheKey newKey = new FeedAggregateCacheKey(aggregateId);

        write(new ConnectorCacheKey<FeedAggregateCacheKey>(newKey), feedEntries, currentTime);
        purge(currentTime);
    }

    public List<FeedEntryDTO> get(String aggregateId) {
        long currentTime = System.currentTimeMillis();
        FeedAggregateCacheKey newKey = new FeedAggregateCacheKey(aggregateId);

        List<FeedEntryDTO> result = read(new ConnectorCacheKey<FeedAggregateCacheKey>(newKey), currentTime);
        purge(currentTime);
        return result;
    }

    protected long getIdleTimeoutSeconds() {
        return CACHE_IDLE_TIMEOUT_SECONDS;
    }

    public static class FeedAggregateCacheKey implements Serializable {
        private String aggregateId;

        public FeedAggregateCacheKey(String aggregateId) {
            this.aggregateId = aggregateId;
        }

        public String getAggregateId() {
            return aggregateId;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FeedAggregateCacheKey that = (FeedAggregateCacheKey) o;

            if (!aggregateId.equals(that.aggregateId)) return false;

            return true;
        }

        public int hashCode() {
            return aggregateId.hashCode();
        }
    }


}
