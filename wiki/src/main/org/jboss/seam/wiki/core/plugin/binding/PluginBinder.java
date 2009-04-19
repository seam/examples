/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.binding;

import org.jboss.seam.wiki.core.plugin.PluginRegistry;

/**
 * Abstract plugin binding contract.
 * <p>
 * Implementations are responsible for reading plugin metadata (XML files,
 * annotations, whatever) and registering metamodel data on the registry.
 * </p>
 * <p>
 * Plugins are first bound, then validated. Subclasses must implement the
 * binding procedure and can extend the validation procedure.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class PluginBinder {

    public void installPlugins(PluginRegistry registry) {
        bindPlugins(registry);
        validatePlugins(registry);
    }

    protected abstract void bindPlugins(PluginRegistry registry);

    protected void validatePlugins(PluginRegistry registry) {
        // TODO: Validate min/max applicationVersions
    }

}
