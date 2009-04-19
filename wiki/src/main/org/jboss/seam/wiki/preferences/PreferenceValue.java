package org.jboss.seam.wiki.preferences;

import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;

public interface PreferenceValue {

    public Object getValue();
    public void setValue(Object value);

    // Useful for provider, only set if the value really changed
    public boolean isDirty();

    // Reference to meta model
    public void setPreferenceProperty(PreferenceEntity.Property property);
    public PreferenceEntity.Property getPreferenceProperty();

}