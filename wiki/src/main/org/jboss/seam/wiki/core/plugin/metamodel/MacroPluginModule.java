/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.metamodel;

import org.jboss.seam.wiki.core.plugin.metamodel.Plugin;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;

import java.util.*;

/**
 * @author Christian Bauer
 */
public class MacroPluginModule extends PluginModule {

    private String name;
    private DocumentArea[] applicableTo = {DocumentArea.CONTENT};
    private RenderOption[] renderOptions;
    private RenderDependency[] renderDependencies;
    private PreferenceEntity preferenceEntity;
    private SortedSet<PreferenceEntity.Property> parameters = new TreeSet<PreferenceEntity.Property>();

    public MacroPluginModule(Plugin plugin, String key) {
        super(plugin, key);
    }

    // TODO: This is currently not used at all, implement it in metadata/xsd/renderer
    public static enum DocumentArea {
        HEADER, CONTENT, FOOTER
    }

    public static enum RenderOption {
        SINGLETON
    }

    // TODO: This is currently not used at all, implement it in metadata/xsd/renderer
    public static class RenderDependency {

        private String onMacro;
        private Type type = Type.REQUIRED;

        public RenderDependency(String onMacro, Type type) {
            this.onMacro = onMacro;
            this.type = type;
        }

        public String getOnMacro() {
            return onMacro;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            REQUIRED, INCOMPATIBLE
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DocumentArea[] getApplicableTo() {
        return applicableTo;
    }

    public void setApplicableTo(DocumentArea[] applicableTo) {
        this.applicableTo = applicableTo;
    }

    public boolean isApplicableTo(DocumentArea area) {
        for (DocumentArea documentArea : getApplicableTo()) {
            if (area.equals(documentArea)) return true;
        }
        return false;
    }

    public RenderOption[] getRenderOptions() {
        return renderOptions;
    }

    public void setRenderOptions(RenderOption[] renderOptions) {
        this.renderOptions = renderOptions;
    }

    public boolean isRenderOptionSet(RenderOption renderOption) {
        if (getRenderOptions() == null) return false;
        for (RenderOption option : getRenderOptions()) {
            if (renderOption.equals(option)) return true;
        }
        return false;
    }

    public RenderDependency[] getRenderDependencies() {
        return renderDependencies;
    }

    public void setRenderDependencies(RenderDependency[] renderDependencies) {
        this.renderDependencies = renderDependencies;
    }

    public PreferenceEntity getPreferenceEntity() {
        return preferenceEntity;
    }

    public void setPreferenceEntity(PreferenceEntity preferenceEntity) {
        this.preferenceEntity = preferenceEntity;
        for (PreferenceEntity.Property property : preferenceEntity.getPropertiesInstanceVisible()) {
            parameters.add(property);
        }
    }

    public SortedSet<PreferenceEntity.Property> getParameters() {
        return parameters;
    }

    public List<PreferenceEntity.Property> getParametersAsList() {
        return Collections.unmodifiableList(new ArrayList(parameters));
    }

    // TODO: This is only used in the Administration UI
    public String getModuleTypeLabel() {
        return "Macro";
    }
}
