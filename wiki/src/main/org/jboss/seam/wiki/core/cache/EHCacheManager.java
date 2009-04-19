/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.cache;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Logging;
import org.jboss.seam.log.LogProvider;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.management.ManagementService;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

/**
 * Seam-managed EHCache backend, starts and stops one CacheManager per application.
 *
 * @author Christian Bauer
 */
@BypassInterceptors
public class EHCacheManager {

    private static final LogProvider log = Logging.getLogProvider(EHCacheManager.class);

    private CacheManager manager;
    private boolean registerMonitoring = true;

    public boolean isRegisterMonitoring() {
        return registerMonitoring;
    }

    public void setRegisterMonitoring(boolean registerMonitoring) {
        this.registerMonitoring = registerMonitoring;
    }

    @Create
    public void initCacheManager() {
        log.info("instantiating EHCacheManager from /ehcache.xml");
        // Do NOT use the CacheManage.create() factory methods, as they create a singleton! Our applicatoin
        // has to have its own CacheManager instance, so that we can run several applications in the same
        // JVM or application server.
        manager = new CacheManager();

        if (isRegisterMonitoring()) {
            // Register statistics MBean of EHCache on the current MBean server
            log.info("registering EHCache monitoring MBean");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ManagementService.registerMBeans(manager, mBeanServer, false, false, false, true);
        }
    }

    @Destroy
    public void shutdownCacheManager() {
        log.info("shutting down EHCacheManager");
        manager.shutdown();
        manager = null;
    }

    @Unwrap
    public CacheManager getCacheManager() {
        return manager;
    }

    public static CacheManager instance() {
        if (!Contexts.isApplicationContextActive()) {
            throw new IllegalStateException("No active application scope");
        }
        return (CacheManager)Component.getInstance("ehCacheManager");
    }
}
