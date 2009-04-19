/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.wiki.preferences.*;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.hibernate.validator.InvalidValue;

import java.util.*;
import java.io.Serializable;

/**
 * Inline editor for preference values, e.g. during document edit.
 * <p>
 * Fires the event <tt>PreferenceEditor.refresh.seamNameOfThePreferenceComponent</tt> after the values
 * of a preference components are changed, used inside a conversation to re-read the state from the
 * edited prefrence value holders into the preference component instances.
 * </p>
 * <p>
 * Fires the event <tt>PreferenceComponent.refresh.seamNameOfThePreferenceComponent</tt> after the
 * preference component instances are refreshed. This allows clients of theses components to reload
 * their state, e.g. re-render themselves with new settings.
 * </p>
 * @author Christian Bauer
 */
public class PluginPreferenceEditor implements Serializable {
/* TODO: We need to implement a new macro editor
    private String pluginPreferenceName;
    private PreferenceComponent preferenceComponent;
    private List<PreferenceValue> preferenceValues = new ArrayList<PreferenceValue>();

    public PluginPreferenceEditor(String pluginPreferenceName) {
        this.pluginPreferenceName = pluginPreferenceName;

        // Load the preference component
        PreferenceRegistry registry = (PreferenceRegistry) Component.getInstance(PreferenceRegistry.class);
        preferenceComponent = registry.getPreferenceComponentsByName().get(pluginPreferenceName);

        if (preferenceComponent != null) {
            // Materialize its values

            Object user = Component.getInstance("currentUser");
            Object instance = Component.getInstance("currentDocument");
            PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");

            preferenceValues = new ArrayList<PreferenceValue>(provider.load(preferenceComponent, user, instance, true));
        }

    }

    public PreferenceComponent getPreferenceComponent() {
        return preferenceComponent;
    }

    public List<PreferenceValue> getPreferenceValues() {
        return preferenceValues;
    }

    public void apply() {

        if (preferenceValues.size() > 0 ) {

            boolean validationOk = true;
            Map<PreferenceProperty, InvalidValue[]> invalidProperties = preferenceComponent.validate(preferenceValues);
            for (Map.Entry<PreferenceProperty, InvalidValue[]> entry : invalidProperties.entrySet()) {
                for (InvalidValue validationError : entry.getValue()) {
                    validationOk = false;
                    FacesMessages.instance().addToControlFromResourceBundleOrDefault(
                        pluginPreferenceName,
                        FacesMessage.SEVERITY_ERROR,
                        "preferenceValueValidationFailed." + preferenceComponent.getName() + "." + entry.getKey().getName(),
                        "'" + entry.getKey().getDescription() + "': " + validationError.getMessage());
                }
            }

            if (validationOk) {
                Object user = Component.getInstance("currentUser");
                Object instance = Component.getInstance("currentDocument");
                PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");
                preferenceValues = new ArrayList<PreferenceValue>(provider.store(preferenceComponent, new HashSet<PreferenceValue>(preferenceValues), user, instance));

                // Reload the preference values from the edited value holders into the preference component instance
                Events.instance().raiseEvent("PreferenceEditor.refresh." + preferenceComponent.getName());

                // Notify users of the preference component
                Events.instance().raiseEvent("PreferenceComponent.refresh." + preferenceComponent.getName());
            }
        }

    }

    public void flush() {
        PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");
        provider.flush();
    }

    @Name("pluginPreferenceEditorFlushObserver")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class FlushObserver implements Serializable {

        Set<PluginPreferenceEditor> editors = new HashSet<PluginPreferenceEditor>();

        public void addPluginPreferenceEditor(PluginPreferenceEditor editor) {
            editors.add(editor);
        }

        @Observer(value = "PreferenceEditor.flushAll", create = false)
        public void flushEditors() {
            for (PluginPreferenceEditor editor : editors) {
                editor.flush();
            }
        }
    }

    */
}
