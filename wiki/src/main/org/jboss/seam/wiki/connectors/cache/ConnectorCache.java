/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.cache;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.log.Log;

import java.util.*;

/**
 * Caches lists of stuff and can asynchronously call a connector, upon configurable cache timeout, to
 * refresh these lists.
 * <p>
 * All method calls to this application-scoped component are synchronized by Seam. That means you might
 * get an <tt>IllegalStateException</tt> when an exclusive lock couldn't be aquired by a thread (because
 * some other thread was already accessing this instance). It's up to the caller to handle that exception,
 * usually you'd log and swallow it and continue without the result of the cache.
 * </p>
 *
 * @author Christian Bauer
 */
@Scope(ScopeType.APPLICATION)
@Synchronized(timeout = 5000)
public abstract class ConnectorCache<T, K> {

    @Logger
    protected Log log;

    private Map<ConnectorCacheKey<K>, List<T>> cache = new HashMap<ConnectorCacheKey<K>, List<T>>();

    protected List<T> lookup(ConnectorCacheKey<K> key) {

        long currentTime = System.currentTimeMillis();
        ConnectorCacheKey<K> cacheKey = findKey(key);

        List<T> result = Collections.EMPTY_LIST;

        if (cacheKey == null) {
            log.debug("cache miss, retrieving it from connector, asynchronously: " + isFirstCacheMissResolvedAsynchronously());

            // The following operations modify the structure of the cache map, which is ok because this
            // method is synchronized by Seam. The write triggered (later) by the AysncUpdater is not
            // synchronized, but we never modify the structure of the cache map then, only now.
            if (getAsyncUpdater() != null && isFirstCacheMissResolvedAsynchronously()) {

                // Write an empty list into the cache
                write(key, Collections.EMPTY_LIST, currentTime);

                // Now start the asynchronous update
                getAsyncUpdater().updateCacheAsynchronously(this, key);

            } else {

                // If we don't have it cached, the (probably first) caller needs to wait (not asynchronous) until we are done
                result = udpateCacheSynchronously(this, key);
                write(key, result, currentTime);
            }

        } else {
            log.debug("cache hit");

            if (getUpdateTimeoutSeconds() != 0) {

                log.debug("checking age of cached entry against update timeout");
                // Check updateTimestamp of cached entry
                if (currentTime - cacheKey.getUpdateTimestamp() > (getUpdateTimeoutSeconds()*1000) ) {
                    log.debug("cached entry is older than maximum cache time, refreshing...");

                    // Start asynchronous updating, might take a while - but should never take longer than cache timeout!
                    getAsyncUpdater().updateCacheAsynchronously(this, cacheKey);

                    // Meanwhile, update the timestamp so that the next caller doesn't also start asynchronous updating
                    // .. we expect to be finished with that before the next caller runs into a cache timeout again!
                    cacheKey.setAccessTimestamp(currentTime);
                    cacheKey.setUpdateTimestamp(currentTime);

                } else {
                    log.debug("cached entry is still inside maximum cache time");
                }
            }

            // Read the value from the cache
            result = read(cacheKey, currentTime);

        }

        // Remove anything with too old accessTimestamp
        purge(currentTime);

        return result;
    }

    protected void write(ConnectorCacheKey<K> key, List<T> list, long currentTime) {
        key.setUpdateTimestamp(currentTime);
        key.setAccessTimestamp(currentTime);
        cache.put(key, list);
    }

    protected List<T> read(ConnectorCacheKey<K> key, long currentTime) {
        key.setAccessTimestamp(currentTime);
        return cache.get(key);
    }

    protected void purge(long currentTime) {
        Set<ConnectorCacheKey<K>> outdatedConnectorCacheKeys = new HashSet<ConnectorCacheKey<K>>();
        Set<ConnectorCacheKey<K>> keyset = cache.keySet();
        for (ConnectorCacheKey key : keyset) {
            if (currentTime - key.getAccessTimestamp() > (getIdleTimeoutSeconds()*1000)) {
                log.debug("removing old cache entry, last accessed: " + key.getAccessTimestamp());
                outdatedConnectorCacheKeys.add(key);
            }
        }
        for (ConnectorCacheKey outdatedConnectorCacheKey : outdatedConnectorCacheKeys)
            cache.remove(outdatedConnectorCacheKey);
    }

    protected ConnectorCacheAsyncUpdater<T, K> getAsyncUpdater() {
        return (ConnectorCacheAsyncUpdater<T, K>) Component.getInstance(getAsyncUpdaterClass());
    }

    protected ConnectorCacheKey<K> findKey(ConnectorCacheKey<K> key) {
        for (ConnectorCacheKey keyOfMap : cache.keySet()) {
            if (keyOfMap.equals(key)) return keyOfMap;
        }
        return null;
    }

    protected long getUpdateTimeoutSeconds() { return 0; }
    protected abstract long getIdleTimeoutSeconds();
    protected Class<? extends ConnectorCacheAsyncUpdater<T, K>> getAsyncUpdaterClass() { return null; }
    protected boolean isFirstCacheMissResolvedAsynchronously() { return true; }
    protected List<T> udpateCacheSynchronously(ConnectorCache<T, K> cache, ConnectorCacheKey<K> key) {
        return Collections.EMPTY_LIST;
    }

}