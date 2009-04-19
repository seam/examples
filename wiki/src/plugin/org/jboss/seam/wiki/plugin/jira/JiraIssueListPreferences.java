/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.jira;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Length;
import org.hibernate.validator.Range;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Preferences(
    name = "JiraIssueList",
    description = "#{messages['jira.issueList.preferences.description']}",
    mappedTo = "jira.issueList"
)
public class JiraIssueListPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['jira.issueList.preferences.property.title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['jira.issueList.preferences.property.url']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String url;

    @PreferenceProperty(
        description = "#{messages['jira.issueList.preferences.property.username']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String username;

    @PreferenceProperty(
        description = "#{messages['jira.issueList.preferences.property.filterId']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String password;

    @PreferenceProperty(
        description = "#{messages['jira.issueList.preferences.property.password']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 50)
    private String filterId;

    @PreferenceProperty(
        description = "#{messages['jira.issueList.preferences.property.numberOfIssues']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 99l)
    @NotNull
    private Long numberOfIssues;

    @PreferenceProperty(
        description = "#{messages['jira.issueList.preferences.property.truncateSummary']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 10l, max = 100l)
    @NotNull
    private Long truncateSummary;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFilterId() {
        return filterId;
    }

    public Long getNumberOfIssues() {
        return numberOfIssues;
    }

    public Long getTruncateSummary() {
        return truncateSummary;
    }
}
