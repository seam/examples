/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.binding.lacewiki;

import org.dom4j.Element;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.core.plugin.PluginCacheManager;
import org.jboss.seam.wiki.core.plugin.metamodel.MacroPluginModule;
import org.jboss.seam.wiki.core.plugin.metamodel.*;
import org.jboss.seam.wiki.core.plugin.metamodel.PluginModule;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;

import java.util.*;

/**
 * Parses and binds plugin.xml metadata to plugin metamodel.
 *
 * @author Christian Bauer
 */
@Name("pluginBinder")
@Scope(ScopeType.APPLICATION)
@Startup
public class PluginBinder extends org.jboss.seam.wiki.core.plugin.binding.PluginBinder {

    @Logger
    Log log;

    @In
    PreferenceRegistry preferenceRegistry;

    @In
    Map<String,String> messages;

    @Create
    public void readDescriptors() {
        log.debug("reading deployment descriptors as XML...");
        descriptors = PluginDeploymentHandler.instance().getDescriptorsAsXmlElements();
    }

    Map<String, Element> descriptors = new HashMap<String, Element>();

    public void bindPlugins(PluginRegistry registry) {
        log.debug("installing plugins from XML descriptors: " + descriptors.size());

        for (Map.Entry<String, Element> descriptor : descriptors.entrySet()) {
            log.debug("installing deployment descriptor: " + descriptor.getKey());
            Element root = descriptor.getValue();

            String pluginKey = root.attributeValue("key");

            log.debug("binding plugin: " + descriptor.getKey());
            Plugin plugin = new Plugin(descriptor.getKey(), pluginKey);
            log.debug("plugin descriptor package path: " + plugin.getDescriptorPackagePath());
            registry.addPlugin(pluginKey, plugin);

            String pluginLabel = root.attributeValue("label");
            if (pluginLabel == null) pluginLabel = getMessage(plugin.getKey()+".label");
            plugin.setLabel(pluginLabel);

            bindPluginInfo(root, plugin);
            bindMacroPluginModules(registry, root, plugin);
            bindProfilePluginModules(registry, root, plugin);
        }

        bindMacroParameters(registry);
    }

    private void bindPluginInfo(Element root, Plugin plugin) {
        List<Element> pluginInfos = root.elements("plugin-info");
        if (pluginInfos.size() == 1) {
            PluginInfo pluginInfo = new PluginInfo();

            pluginInfo.setVersion(pluginInfos.get(0).attributeValue("version"));

            String description = pluginInfos.get(0).attributeValue("description");
            if (description == null) description = getMessage(plugin.getKey()+".description");
            pluginInfo.setDescription(description);

            List<Element> applicationVersions = pluginInfos.get(0).elements("application-version");
            if (applicationVersions.size() == 1) {
                pluginInfo.setApplicationVersion(
                    applicationVersions.get(0).attributeValue("min"),
                    applicationVersions.get(0).attributeValue("max")
                );
            }

            List<Element> vendors = pluginInfos.get(0).elements("vendor");
            if (vendors.size() == 1) {
                pluginInfo.setVendor(
                    vendors.get(0).attributeValue("name"),
                    vendors.get(0).attributeValue("url")
                );
            }
            
            plugin.setPluginInfo(pluginInfo);
        }
    }

    private void bindMacroPluginModules(PluginRegistry registry, Element root, Plugin plugin) {

        // Iterate through the XML descriptor and bind every <macro> to corresponding metamodel instances
        List<Element> macroPlugins = root.elements("macro");
        for (Element descriptor : macroPlugins) {

            String moduleKey = descriptor.attributeValue("key");
            MacroPluginModule module = new MacroPluginModule(plugin, moduleKey);

            log.debug("binding macro plugin module: " + module.getFullyQualifiedKey());

            bindLabelDescription(descriptor, module, plugin);
            bindMacroApplicableTo(descriptor, module);
            bindMacroRenderOptions(descriptor, module);
            bindSkins(descriptor, module);
            bindCacheRegions(descriptor, module);

            String macroName = descriptor.attributeValue("name");
            if (registry.getMacroPluginModulesByMacroName().containsKey(macroName)) {
                throw new InvalidWikiConfigurationException("Duplicate macro name, needs to be globally unique: " + macroName);
            }
            module.setName(macroName);

            // Finally, bind it
            plugin.getModules().add(module);
            registry.getMacroPluginModulesByKey().put(module.getFullyQualifiedKey(), module);
            registry.getMacroPluginModulesByMacroName().put(module.getName(), module);
        }

    }

    private void bindProfilePluginModules(PluginRegistry registry, Element root, Plugin plugin) {

        // Iterate through the XML descriptor and bind every <profile> to corresponding metamodel instances
        List<Element> profilePlugins = root.elements("profile");
        for (Element descriptor : profilePlugins) {

            String moduleKey = descriptor.attributeValue("key");
            ProfilePluginModule module = new ProfilePluginModule(plugin, moduleKey);

            log.debug("binding profile plugin module: " + module.getFullyQualifiedKey());

            bindLabelDescription(descriptor, module, plugin);
            bindSkins(descriptor, module);

            module.setTemplate(descriptor.attributeValue("template"));
            module.setPriority(new Integer(descriptor.attributeValue("priority")));

            // Finally, bind it
            plugin.getModules().add(module);
            registry.getProfilePluginModulesByPriority().add(module);
        }

    }

    private void bindLabelDescription(Element descriptor, PluginModule module, Plugin plugin) {
        String label = descriptor.attributeValue("label");
        if (label == null) label = getMessage(plugin.getKey() + "." + module.getKey() + ".label");
        module.setLabel(label);
        String description = descriptor.attributeValue("description");
        if (description == null) description = getMessage(plugin.getKey() + "." + module.getKey() + ".description");
        module.setDescription(description);
    }

    private void bindCacheRegions(Element descriptor, PluginModule module) {
        Element cacheRegionsDescriptor = descriptor.element("cache-regions");
        if (cacheRegionsDescriptor != null) {
            List<Element> cacheRegions = cacheRegionsDescriptor.elements("cache-region");
            if (cacheRegions.size() > 0) {
                for (Element cacheRegion : cacheRegions) {
                    String unqualifiedCacheRegionName = cacheRegion.attributeValue("name");
                    module.addFragmentCacheRegion(unqualifiedCacheRegionName);

                    List<Element> invalidationEvents = cacheRegion.elements("invalidation-event");
                    if (invalidationEvents != null) {
                        for (Element invalidationEvent : invalidationEvents) {
                            String eventName = invalidationEvent.attributeValue("name");
                            PluginCacheManager.registerBinding(
                                    eventName,
                                    module.getQualifiedCacheRegionName(unqualifiedCacheRegionName)
                            );
                        }
                    }
                }
            }
        }
    }

    private void bindSkins(Element descriptor, PluginModule module) {
        Element skins = descriptor.element("skins");
        if (skins != null) bindSkin(skins, module);
    }

    private void bindSkin(Element descriptor, PluginModule module) {
        List<Element> skins = descriptor.elements("skin");
        if (skins.size() > 0) {
            String[] skinNames = new String[skins.size()];
            for (int i = 0; i < skins.size(); i++)
                skinNames[i] = skins.get(i).attributeValue("name");
            module.setSkins(skinNames);
        }
    }

    private void bindMacroApplicableTo(Element descriptor, MacroPluginModule module) {
        Element applicableTo = descriptor.element("applicable-to");
        if (applicableTo != null) {
            boolean header = Boolean.parseBoolean(applicableTo.attributeValue("header"));
            boolean content = Boolean.parseBoolean(applicableTo.attributeValue("content"));
            boolean footer = Boolean.parseBoolean(applicableTo.attributeValue("footer"));
            List<MacroPluginModule.DocumentArea> applicableList = new ArrayList<MacroPluginModule.DocumentArea>();
            if (header) applicableList.add(MacroPluginModule.DocumentArea.HEADER);
            if (content) applicableList.add(MacroPluginModule.DocumentArea.CONTENT);
            if (footer) applicableList.add(MacroPluginModule.DocumentArea.FOOTER);
            MacroPluginModule.DocumentArea[] applicableArray = new MacroPluginModule.DocumentArea[applicableList.size()];
            module.setApplicableTo(applicableList.toArray(applicableArray));
        }
    }

    private void bindMacroRenderOptions(Element descriptor, MacroPluginModule module) {
        Element renderOptions = descriptor.element("render-options");
        if (renderOptions != null) {
            List<MacroPluginModule.RenderOption> renderOptionList =
                        new ArrayList<MacroPluginModule.RenderOption>();
            List<Element> options = renderOptions.elements();
            for (Element option : options) {
                if (option.getName().equals("singleton"))
                    renderOptionList.add(MacroPluginModule.RenderOption.SINGLETON);
            }
            MacroPluginModule.RenderOption[] renderOptionArray =
                    new MacroPluginModule.RenderOption[renderOptionList.size()];
            module.setRenderOptions(renderOptionList.toArray(renderOptionArray));
        }

    }

    private void bindMacroParameters(PluginRegistry registry) {
        // Iterate through all @Preference entities, look for instanceType() values (which are fully qualified macro keys)
        Set<PreferenceEntity> entitiesWithInstanceProperties =
                preferenceRegistry.getPreferenceEntities(PreferenceVisibility.INSTANCE);
        for (PreferenceEntity entity : entitiesWithInstanceProperties) {
            if (entity.getMappedTo() != null && entity.getMappedTo().length() > 0) {
                // All INSTANCE properties of this entity are for one macro, ignore whatever is configured on the properties
                String macroPluginKey = entity.getMappedTo();
                MacroPluginModule macroPluginModule = registry.getMacroPluginModulesByKey().get(macroPluginKey);
                if (macroPluginModule != null) {
                    log.debug("binding all INSTANCE properties as parameters of macro '" + macroPluginModule.getName() +"': " + entity);
                    macroPluginModule.setPreferenceEntity(entity);
                } else {
                    throw new InvalidWikiConfigurationException(
                        "Configured mapping to macro module '"+macroPluginKey+"' not found for " + entity
                    );
                }
            } else {
                // Some INSTANCE properties are for one macro, others for other macros, so we need to mix and match
                for (PreferenceEntity.Property property : entity.getPropertiesInstanceVisible()) {
                    if (property.getMappedTo() == null || property.getMappedTo().length() == 0) continue;
                    
                    String macroPluginKey = property.getMappedTo();
                    MacroPluginModule macroPluginModule = registry.getMacroPluginModulesByKey().get(macroPluginKey);
                    if (macroPluginModule != null) {
                        log.debug("binding INSTANCE property as parameter of macro '" + macroPluginModule.getName() +"': " + property);
                        macroPluginModule.setPreferenceEntity(entity);
                        macroPluginModule.getParameters().add(property);
                    } else {
                        throw new InvalidWikiConfigurationException(
                            "Configured mapping to macro module  '"+macroPluginKey+"' not found for " + property + " in " + entity
                        );
                    }
                }
            }
        }
    }

    private String getMessage(String key) {
        String message = messages.get(key);
        if (message.equals(key)) {
            throw new InvalidWikiConfigurationException("Could not find message key for label/description: " + key);
        }
        return message;
    }

}
