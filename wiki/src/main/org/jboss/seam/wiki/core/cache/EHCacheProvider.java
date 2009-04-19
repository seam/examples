/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.cache;

import org.hibernate.cache.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Properties;

import net.sf.ehcache.CacheManager;

/**
 * Cache provider for Hibernate that utilizes Seam-managed EHCache backend.
 *
 * @author Christian Bauer
 */
public class EHCacheProvider implements CacheProvider {

    private static final Log log = LogFactory.getLog(EHCacheProvider.class);

    protected CacheManager getCacheManager() {
        return EHCacheManager.instance();
    }

    public void start(Properties properties) throws CacheException {
        // NOOP, started by EHCacheManager Seam component
    }

    public void stop() {
        // NOOP, destroyed by EHCacheManager Seam component
    }

    public Cache buildCache(String name, Properties properties) throws CacheException {
	    try {
            net.sf.ehcache.Cache cache = getCacheManager().getCache(name);
            if (cache == null) {
                log.warn("Could not find configuration [" + name + "]; using defaults.");
                getCacheManager().addCache(name);
                cache = getCacheManager().getCache(name);
                log.debug("started EHCache region: " + name);
            }
            return new EhCache(cache);
	    }
        catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    public long nextTimestamp() {
        return Timestamper.next();
    }

    public boolean isMinimalPutsEnabledByDefault() {
        return false;
    }
}
