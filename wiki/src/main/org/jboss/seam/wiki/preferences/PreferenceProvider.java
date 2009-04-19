package org.jboss.seam.wiki.preferences;

import java.util.Set;
import java.util.List;

public interface PreferenceProvider<U, I> {

    public Set<PreferenceValue> loadValues(String preferenceEntityName, U user, I instance, List<PreferenceVisibility> visibilities);
    public void storeValues(Set<PreferenceValue> valueHolders, U user, I instance);

    public void deleteUserPreferenceValues(U user);

    public void flush();
}