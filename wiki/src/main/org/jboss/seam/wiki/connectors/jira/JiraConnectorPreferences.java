/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.jira;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */

@Preferences(name = "JiraConnector", description = "#{messages['jiraConnector.preferences.Name']}")
public class JiraConnectorPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['jiraConnector.preferences.ConnectionTimeoutSeconds']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 120l)
    @NotNull
    private Long connectionTimeoutSeconds;

    @PreferenceProperty(
        description = "#{messages['jiraConnector.preferences.ReplyTimeoutSeconds']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 120l)
    @NotNull
    private Long replyTimeoutSeconds;

    @PreferenceProperty(
        description = "#{messages['jiraConnector.preferences.IssueListCacheUpdateTimeoutSeconds']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @Range(min = 1l, max = 864000)
    @NotNull
    private Long issueListCacheUpdateTimeoutSeconds;

    @PreferenceProperty(
        description = "#{messages['jiraConnector.preferences.IssueListCacheIdleTimeoutSeconds']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @Range(min = 1l, max = 864000)
    @NotNull
    private Long issueListCacheIdleTimeoutSeconds;

    public Long getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public Long getReplyTimeoutSeconds() {
        return replyTimeoutSeconds;
    }

    public Long getIssueListCacheUpdateTimeoutSeconds() {
        return issueListCacheUpdateTimeoutSeconds;
    }

    public Long getIssueListCacheIdleTimeoutSeconds() {
        return issueListCacheIdleTimeoutSeconds;
    }
}
