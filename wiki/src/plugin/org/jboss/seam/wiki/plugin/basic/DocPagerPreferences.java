/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Preferences(
    name = "DocPager",
    description = "#{messages['basic.docPager.preferences.description']}",
    mappedTo = "basic.docPager"
)
public class DocPagerPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['basic.docPager.preferences.property.byProperty']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String byProperty;

    @PreferenceProperty(
        description = "#{messages['basic.docPager.preferences.property.showNames']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE}
    )
    @NotNull
    private Boolean showNames;

    public String getByProperty() {
        return byProperty;
    }

    public Boolean getShowNames() {
        return showNames;
    }
}
