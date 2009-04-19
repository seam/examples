/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.cache;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import java.util.List;

/**
 * @author Christian Bauer
 */
@Scope(ScopeType.EVENT)
public abstract class ConnectorCacheAsyncUpdater<T, K> {

    @Logger
    Log log;

    public void writeIntoCache(ConnectorCache<T, K> cache, ConnectorCacheKey<K> key, List<T> result) {
        log.debug("writing data into cache for key: " + key + " size: " + result.size());

        // Note that this write is not synchronized on the APPLICATION-scoped cache component!
        // However, that is ok because the write only updates a value of the map, it does not
        // modify the structure of the map.
        cache.write(key, result, System.currentTimeMillis());
    }

    public abstract void updateCacheAsynchronously(ConnectorCache<T, K> cache, ConnectorCacheKey<K> key);

}

