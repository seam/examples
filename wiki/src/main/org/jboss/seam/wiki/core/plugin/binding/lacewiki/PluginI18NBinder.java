/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.binding.lacewiki;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;

import java.util.List;

/**
 * A factory for <tt>pluginMessageBundleNames</tt> using the
 * LaceWiki deployment layout. The job of this component is
 * to transport the names of all found/deployed message bundles
 * from the deployment scanner/handler into an application-scoped
 * variable, which we can then access when resources have to be loaded.
 *
 * @see PluginI18NDeploymentHandler
 * 
 * @author Christian Bauer
 */
@Name("pluginI18NBinder")
@Scope(ScopeType.APPLICATION)
@Startup
public class PluginI18NBinder {

    @Logger
    Log log;

    private List<String> pluginMessageBundleNames;

    @Create
    public void registerResourceBundleNames() {
        log.debug("registering plugin i18n property files as bundle names");
        pluginMessageBundleNames = PluginI18NDeploymentHandler.instance().getMessageBundleNames();
        if (log.isDebugEnabled()) {
            for (String s : pluginMessageBundleNames) {
                log.debug("registered bundle name: " + s);
            }
        }
    }

    @Factory(value = "pluginMessageBundleNames", autoCreate = true)
    public List<String> getPluginMessageBundleNames() {
        return pluginMessageBundleNames;
    }
}
