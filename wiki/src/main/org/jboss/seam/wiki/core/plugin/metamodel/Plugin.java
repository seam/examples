/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.metamodel;

import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * Plugin metadata.
 *
 * @author Christian Bauer
 */
public class Plugin implements Serializable {

    // Some constants that represent the sub-package layout of a plugin package
    public static final String PACKAGE_I18N = "i18n";
    public static final String PACKAGE_I18N_MESSAGES = "messages";
    public static final String PACKAGE_TEMPLATES = "templates";
    public static final String PACKAGE_THEMES = "themes";
    public static final String PACKAGE_THEMES_CSS = "css";
    public static final String PACKAGE_THEMES_IMG = "img";

    public static final String KEY_PATTERN = "[a-zA-Z0-9]+";

    // Web request paths for resource loading, one for generating URIs, the other for parsing requests
    public static final String GENERATE_RESOURCE_PATH_THEME = "/seam/resource/wikiPluginTheme";
    public static final String REGISTER_SEAM_RESOURCE_THEME = "/wikiPluginTheme";

    private String descriptorPath;
    private String descriptorPackagePath;
    private String key;
    private String label;
    private PluginInfo pluginInfo;
    private List<PluginModule> modules = new ArrayList<PluginModule>();

    public Plugin(String descriptorPath, String key) {
        if (!key.matches(KEY_PATTERN))
            throw new InvalidWikiConfigurationException("Key doesn't match pattern '"+KEY_PATTERN+"': " + key);

        this.descriptorPath = descriptorPath; // '/foo/bar/Baz.plugin.xhtml'
        this.descriptorPackagePath =
                descriptorPath.substring(0, descriptorPath.lastIndexOf("/")); // '/foo/bar'
        this.key = key;
    }

    public String getDescriptorPath() {
        return descriptorPath;
    }

    public String getDescriptorPackagePath() {
        return descriptorPackagePath;
    }

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(PluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public List<PluginModule> getModules() {
        return modules;
    }

    public PluginModule getModuleByKey(String key) {
        for (PluginModule pluginModule : getModules()) {
            if (pluginModule.getKey().equals(key)) return pluginModule;
        }
        return null;
    }

    public void setModules(List<PluginModule> modules) {
        this.modules = modules;
    }

    public String getPackageCSSPath() {
        return getPackageThemePath() + "/" + Plugin.PACKAGE_THEMES_CSS;
    }

    public String getPackageThemePath() {
        String currentTheme = (String) Component.getInstance("currentTheme");
        return getDescriptorPackagePath()
                + "/" + Plugin.PACKAGE_THEMES
                + "/" + currentTheme;
    }

    public String getPackageDefaultTemplatePath(String templateName) {
        return getDescriptorPackagePath()
                + "/" + Plugin.PACKAGE_TEMPLATES
                + "/" + templateName + ".xhtml";
    }

    public String getPackageI18NPath() {
        return getDescriptorPackagePath() + "/" + Plugin.PACKAGE_I18N;
    }
}
