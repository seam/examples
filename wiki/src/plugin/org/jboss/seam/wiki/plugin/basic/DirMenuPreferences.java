/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.hibernate.validator.Length;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Preferences(
    name = "DirMenu",
    description = "#{messages['basic.dirMenu.preferences.description']}",
    mappedTo = "basic.dirMenu"
)
public class DirMenuPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['basic.dirMenu.preferences.property.title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['basic.dirMenu.preferences.property.menuLevels']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 10l)
    @NotNull
    private Long menuLevels;

    @PreferenceProperty(
        description = "#{messages['basic.dirMenu.preferences.property.menuDepth']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"

    )
    @Range(min = 1l, max = 100l)
    @NotNull
    private Long menuDepth;

    @PreferenceProperty(
        description = "#{messages['basic.dirMenu.preferences.property.showSubscribeIcon']}",
        visibility = PreferenceVisibility.INSTANCE
    )
    private Boolean showSubscribeIcon;

    @PreferenceProperty(
            description = "#{messages['basic.dirMenu.preferences.property.onlyMenuItems']}",
        visibility = PreferenceVisibility.INSTANCE
    )
    private Boolean onlyMenuItems;

    public String getTitle() {
        return title;
    }

    public Long getMenuLevels() {
        return menuLevels;
    }

    public Long getMenuDepth() {
        return menuDepth;
    }

    public Boolean getShowSubscribeIcon() {
        return showSubscribeIcon;
    }

    public Boolean getOnlyMenuItems() {
        return onlyMenuItems;
    }
}
