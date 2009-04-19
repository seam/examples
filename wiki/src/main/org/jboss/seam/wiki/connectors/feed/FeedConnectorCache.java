/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import org.jboss.seam.wiki.connectors.cache.ConnectorCache;
import org.jboss.seam.wiki.connectors.cache.ConnectorCacheAsyncUpdater;
import org.jboss.seam.wiki.connectors.cache.ConnectorCacheKey;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.async.Asynchronous;

import java.io.Serializable;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("feedConnectorCache")
@AutoCreate
public class FeedConnectorCache
        extends ConnectorCache<FeedEntryDTO, FeedConnectorCache.FeedConnectorCacheKey>
        implements FeedConnector {

    @In("#{preferences.get('FeedConnector')}")
    FeedConnectorPreferences prefs;

    public List<FeedEntryDTO> getFeedEntries(String feedURL) {
        FeedConnectorCacheKey newKey = new FeedConnectorCacheKey(feedURL);
        return lookup( new ConnectorCacheKey<FeedConnectorCacheKey>(newKey));
    }

    protected long getUpdateTimeoutSeconds() {
        return prefs.getFeedCacheUpdateTimeoutSeconds();
    }

    protected long getIdleTimeoutSeconds() {
        return prefs.getFeedCacheIdleTimeoutSeconds();
    }

    protected Class<? extends ConnectorCacheAsyncUpdater<FeedEntryDTO, FeedConnectorCacheKey>> getAsyncUpdaterClass() {
        return FeedConnectorCacheAsyncUpdater.class;
    }

    @Name("feedConnectorCacheAsyncUpdater")
    @AutoCreate
    public static class FeedConnectorCacheAsyncUpdater
            extends ConnectorCacheAsyncUpdater<FeedEntryDTO, FeedConnectorCacheKey> {

        @In
        FeedConnector feedConnector;

        @Asynchronous
        public void updateCacheAsynchronously(ConnectorCache<FeedEntryDTO, FeedConnectorCacheKey> cache,
                                                 ConnectorCacheKey<FeedConnectorCacheKey> key) {

            List<FeedEntryDTO> result = feedConnector.getFeedEntries(key.getKeyValue().getUrl());
            if (result.size() > 0)
                super.writeIntoCache(cache, key, result);
        }
    }

    public static class FeedConnectorCacheKey implements Serializable {
        private String url;

        public FeedConnectorCacheKey(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FeedConnectorCacheKey that = (FeedConnectorCacheKey) o;

            if (!url.equals(that.url)) return false;

            return true;
        }

        public int hashCode() {
            return url.hashCode();
        }

        public String toString() {
            return getUrl();
        }
    }

}
