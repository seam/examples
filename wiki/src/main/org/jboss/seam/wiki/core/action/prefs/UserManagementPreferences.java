package org.jboss.seam.wiki.core.action.prefs;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Preferences(name = "UserManagement", description = "#{messages['lacewiki.preferences.userManagement.Name']}")
public class UserManagementPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.userManagement.ActivationCodeSalt']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 5, max = 20)
    @NotNull
    private String activationCodeSalt;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.userManagement.PasswordRegex']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 2, max = 100)
    @NotNull
    private String passwordRegex;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.userManagement.NewUserInRole']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "SelectOne",
        templateComponentName = "rolesPreferenceValueTemplate"
    )
    @Length(min = 3, max = 255)
    @NotNull
    private String newUserInRole;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.userManagement.EnableRegistration']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @NotNull
    private Boolean enableRegistration;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.userManagement.CreateHomeAfterUserActivation']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @NotNull
    private Boolean createHomeAfterUserActivation;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.userManagement.HomepageDefaultContent']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 1023)
    private String homepageDefaultContent;

    public String getActivationCodeSalt() {
        return activationCodeSalt;
    }

    public String getPasswordRegex() {
        return passwordRegex;
    }

    public String getNewUserInRole() {
        return newUserInRole;
    }

    public Boolean getEnableRegistration() {
        return enableRegistration;
    }

    public Boolean getCreateHomeAfterUserActivation() {
        return createHomeAfterUserActivation;
    }

    public String getHomepageDefaultContent() {
        return homepageDefaultContent;
    }
}
