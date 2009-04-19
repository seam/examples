/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.cache.PageFragmentCache;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

/**
 * Cleans the right cache region when an event occurs.
 *
 * @author Christian Bauer
 */
@Name("pluginCacheManager")
@AutoCreate
public class PluginCacheManager {

    @Logger
    Log log;

    @In
    PageFragmentCache pageFragmentCache;

    public void invalidateCacheRegion(String region) {
        log.debug("removing all elements from page fragment cache region: " + region);
        pageFragmentCache.removeAll(region);

    }

    public static void registerBinding(String eventType, String region) {
        String binding = "#{pluginCacheManager.invalidateCacheRegion('"+region+"')}";
        Events.instance().addListener(eventType, binding, String.class);
    }

}
