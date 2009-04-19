/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action.prefs;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Preferences(name = "Wiki", description = "#{messages['lacewiki.preferences.wiki.Name']}")
public class WikiPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.BaseURL']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 8, max = 255)
    @NotNull
    private String baseUrl;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.TimeZone']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "SelectOne",
        templateComponentName = "timeZonePreferenceValueTemplate"
    )
    @Length(min = 3, max = 63)
    @NotNull
    private String timeZone;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.ThemeName']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "SelectOne",
        templateComponentName = "themePreferenceValueTemplate"
    )
    @Length(min = 3, max = 255)
    @NotNull
    private String themeName;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.MemberArea']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "SelectOne",
        templateComponentName = "writeProtectedAreaPreferenceValueTemplate"
    )
    @Length(min = 3, max = 255)
    @NotNull
    private String memberArea;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.HelpArea']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "SelectOne",
        templateComponentName = "writeProtectedAreaPreferenceValueTemplate"
    )
    @Length(min = 3, max = 255)
    @NotNull
    private String helpArea;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.TrashArea']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "SelectOne",
        templateComponentName = "writeProtectedAreaPreferenceValueTemplate"
    )
    @Length(min = 3, max = 255)
    @NotNull
    private String trashArea;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.DefaultDocumentId']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @NotNull
    private Long defaultDocumentId;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.RenderPermLinks']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    private Boolean renderPermlinks;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.PermLinkSuffix']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 2, max = 10)
    @org.hibernate.validator.Pattern(regex="\\.[a-zA-z]+")
    @NotNull
    private String permlinkSuffix;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.FeedTitlePrefix']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String feedTitlePrefix;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.PurgeFeedEntriesAfterDays']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @Range(min = 1l, max = 999l)
    @NotNull
    private Long purgeFeedEntriesAfterDays;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.ShowSiteFeedInMenu']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    private Boolean showSiteFeedInMenu;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.AtSymbolReplacement']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 1, max = 20)
    @NotNull
    private String atSymbolReplacement;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.ShowEmailToLoggedInOnly']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    private Boolean showEmailToLoggedInOnly;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.MainMenuLevels']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "NumberRange"
    )
    @Range(min = 0l, max = 10l)
    @NotNull
    private Long mainMenuLevels;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.MainMenuDepth']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 10l)
    @NotNull
    private Long mainMenuDepth;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.MainMenuShowAdminOnly']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    private Boolean mainMenuShowAdminOnly;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.ShowDocumentCreatorHistory']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    private Boolean showDocumentCreatorHistory;
    
    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.ShowTags']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    private Boolean showTags;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.wiki.WorkspaceSwitcherDescriptionLength']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 100l)
    @NotNull
    private Long workspaceSwitcherDescriptionLength;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getThemeName() {
        return themeName;
    }

    public String getMemberArea() {
        return memberArea;
    }

    public String getHelpArea() {
        return helpArea;
    }

    public String getTrashArea() {
        return trashArea;
    }

    public Long getDefaultDocumentId() {
        return defaultDocumentId;
    }

    public Boolean isRenderPermlinks() {
        return renderPermlinks;
    }

    public String getPermlinkSuffix() {
        return permlinkSuffix;
    }

    public String getFeedTitlePrefix() {
        return feedTitlePrefix;
    }

    public Long getPurgeFeedEntriesAfterDays() {
        return purgeFeedEntriesAfterDays;
    }

    public Boolean getShowSiteFeedInMenu() {
        return showSiteFeedInMenu;
    }

    public String getAtSymbolReplacement() {
        return atSymbolReplacement;
    }

    public Boolean isShowEmailToLoggedInOnly() {
        return showEmailToLoggedInOnly;
    }

    public Long getMainMenuLevels() {
        return mainMenuLevels;
    }

    public Long getMainMenuDepth() {
        return mainMenuDepth;
    }

    public Boolean isMainMenuShowAdminOnly() {
        return mainMenuShowAdminOnly;
    }

    public Boolean getShowDocumentCreatorHistory() {
        return showDocumentCreatorHistory;
    }

    public Boolean getShowTags() {
        return showTags;
    }

    public Long getWorkspaceSwitcherDescriptionLength() {
        return workspaceSwitcherDescriptionLength;
    }
}
