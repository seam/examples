package org.jboss.seam.wiki.core.action.prefs;

import org.hibernate.validator.NotNull;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Preferences(name = "Comments", description = "#{messages['lacewiki.preferences.comments.Name']}")
public class CommentsPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.comments.ListAscending']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.USER}
    )
    @NotNull
    private Boolean listAscending;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.comments.EnableByDefault']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.USER}
    )
    @NotNull
    private Boolean enableByDefault;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.comments.Threaded']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.USER}
    )
    @NotNull
    private Boolean threaded;

    public Boolean getListAscending() {
        return listAscending;
    }

    public Boolean getEnableByDefault() {
        return enableByDefault;
    }

    public Boolean getThreaded() {
        return threaded;
    }
}
