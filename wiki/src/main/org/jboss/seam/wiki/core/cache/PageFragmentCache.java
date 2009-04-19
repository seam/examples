/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.core.plugin.metamodel.Plugin;
import org.jboss.seam.wiki.core.plugin.metamodel.PluginModule;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import java.util.*;

/**
 * Override Seam built-in pojoCache component with EHCache implementation.
 * <p>
 * This component creates cache regions on startup. The regions are obtained from the
 * <tt>pluginRegistry</tt> and each plugin module. If desired, individual 
 * </p>
 *
 * @author Christian Bauer
 */
@BypassInterceptors
public class PageFragmentCache {

    private static final LogProvider log = Logging.getLogProvider(PageFragmentCache.class);

    // This is threadsafe
    Map<String, Cache> caches = new HashMap<String, Cache>();

    List<String> cacheRegions;

    public List<String> getCacheRegions() {
        return cacheRegions;
    }

    public void setCacheRegions(List<String> cacheRegions) {
        this.cacheRegions = cacheRegions;
    }

    @Create
    public void startup() throws Exception {

        log.info("starting wiki page fragment cache regions");
        try {
            CacheManager manager = EHCacheManager.instance();

            Set<String> requiredCacheRegions = new HashSet<String>();

            if (cacheRegions != null) {
                requiredCacheRegions.addAll(cacheRegions);
            }

            PluginRegistry pluginRegistry = PluginRegistry.instance();
            for (Plugin plugin : pluginRegistry.getPlugins()) {
                for (PluginModule pluginModule : plugin.getModules()) {
                    if (pluginModule.getFragmentCacheRegions() != null)
                        requiredCacheRegions.addAll(pluginModule.getFragmentCacheRegions());
                }
            }

            for (String cacheRegion : requiredCacheRegions) {
                Cache cache = EHCacheManager.instance().getCache(cacheRegion);
                if (cache == null) {
                    log.info("using default configuration for region '" + cacheRegion + "'");
                    manager.addCache(cacheRegion);
                    cache = manager.getCache(cacheRegion);
                    log.debug("started EHCache region: " + cacheRegion);
                }
                caches.put(cacheRegion, cache);
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void put(String region, String key, String content) {
        if (!caches.containsKey(region))
            throw new IllegalStateException("can't put into uninitialized cache region: " + region);
        caches.get(region).put(new Element(key, content));
    }

    public String get(String region, String key) {
        if (!caches.containsKey(region))
            throw new IllegalStateException("can't get from uninitialized cache region: " + region);
        Element result = caches.get(region).get(key);
        return result != null ? (String)result.getValue() : null;
    }

    public void remove(String region, String key) {
        if (!caches.containsKey(region))
            throw new IllegalStateException("can't remove from uninitialized cache region: " + region);
        caches.get(region).remove(key);
    }

    public void removeAll(String region) {
        if (!caches.containsKey(region))
            throw new IllegalStateException("can't remove all from uninitialized cache region: " + region);
        caches.get(region).removeAll();
    }

    public static PageFragmentCache instance() {
        if (!Contexts.isApplicationContextActive()) {
            throw new IllegalStateException("No active application scope");
        }
        return (PageFragmentCache) Component.getInstance("pageFragmentCache", ScopeType.APPLICATION);
    }

}
