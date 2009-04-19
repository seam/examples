/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.plugin.metamodel.Plugin;
import org.jboss.seam.wiki.core.plugin.metamodel.MacroPluginModule;
import org.jboss.seam.wiki.core.plugin.metamodel.ProfilePluginModule;
import org.jboss.seam.wiki.core.plugin.binding.PluginBinder;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;
import org.jboss.seam.wiki.core.model.WikiTextMacro;

import java.util.*;


/**
 * @author Christian Bauer
 */
@Name("pluginRegistry")
@Scope(ScopeType.APPLICATION)
@Startup(depends = "preferenceRegistry")
@BypassInterceptors
public class PluginRegistry {

    public static final Class[] PLUGIN_BINDERS = {
        org.jboss.seam.wiki.core.plugin.binding.lacewiki.PluginBinder.class
    };

    private static final LogProvider log = Logging.getLogProvider(PluginRegistry.class);

    private SortedMap<String, Plugin> plugins = new TreeMap<String, Plugin>();
    private SortedMap<String, MacroPluginModule> macroPluginModulesByKey = new TreeMap<String, MacroPluginModule>();
    private SortedMap<String, MacroPluginModule> macroPluginModulesByMacroName = new TreeMap<String, MacroPluginModule>();
    private SortedSet<ProfilePluginModule> profilePluginModulesByPriority = new TreeSet<ProfilePluginModule>();

    public void addPlugin(String key, Plugin p) {
        if (plugins.containsKey(key)) {
            throw new InvalidWikiConfigurationException("Duplicate plugin key: " + key);
        }
        plugins.put(key, p);
    }

    public Plugin removePlugin(String key) {
        return plugins.remove(key);
    }

    public Plugin getPlugin(String key) {
        return plugins.get(key);
    }

    public List<Plugin> getPlugins() {
        return Collections.unmodifiableList(new ArrayList(plugins.values()));
    }

    public SortedMap<String, Plugin> getPluginsByKey() {
        return Collections.unmodifiableSortedMap(plugins);
    }

    // TODO: This should be unmodifiable and additions only allowed through a new registry method
    public SortedMap<String, MacroPluginModule> getMacroPluginModulesByKey() {
        return macroPluginModulesByKey;
    }

    // TODO: This should be unmodifiable and additions only allowed through a new registry method
    public SortedMap<String, MacroPluginModule> getMacroPluginModulesByMacroName() {
        return macroPluginModulesByMacroName;
    }

    // TODO: This should be unmodifiable and additions only allowed through a new registry method
    public SortedSet<ProfilePluginModule> getProfilePluginModulesByPriority() {
        return profilePluginModulesByPriority;
    }

    public List<ProfilePluginModule> getProfilePluginModulesAsList() {
        return new ArrayList<ProfilePluginModule>(profilePluginModulesByPriority);
    }

    public WikiPluginMacro createWikiPluginMacro(WikiTextMacro wikiTextMacro) {
        if (getMacroPluginModulesByMacroName().containsKey(wikiTextMacro.getName())) {
            log.debug("binding WikiTextMacro metadata to create WikiPluginMacro instance: " + wikiTextMacro);
            return new WikiPluginMacro( getMacroPluginModulesByMacroName().get(wikiTextMacro.getName()), wikiTextMacro );
        } else {
            return null;
        }
    }

    @Create
    public void startup() {
        log.info("initializing plugin registry");

        for (Class pluginBinderClass : PLUGIN_BINDERS) {
            log.debug("calling plugin binder: " + pluginBinderClass.getName());
            PluginBinder pluginBinder = (PluginBinder)Component.getInstance(pluginBinderClass);
            pluginBinder.installPlugins(this);
        }

        log.info("registered plugins: " + plugins.size());
    }

    public static PluginRegistry instance() {
        return (PluginRegistry) Component.getInstance(PluginRegistry.class);
    }

}
