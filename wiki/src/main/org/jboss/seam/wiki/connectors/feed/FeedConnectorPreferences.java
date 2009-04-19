/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;

/**
 * @author Christian Bauer
 */
@Preferences(name = "FeedConnector", description = "#{messages['feedConnector.preferences.Name']}")
public class FeedConnectorPreferences {

    @PreferenceProperty(
        description = "#{messages['feedConnector.preferences.ConnectionTimeoutSeconds']}",
        visibility = PreferenceVisibility.SYSTEM,
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 120l)
    @NotNull
    private Long connectionTimeoutSeconds;

    @PreferenceProperty(
        description = "#{messages['feedConnector.preferences.FeedCacheUpdateTimeoutSeconds']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @Range(min = 1l, max = 864000)
    @NotNull
    private Long feedCacheUpdateTimeoutSeconds;

    @PreferenceProperty(
        description = "#{messages['feedConnector.preferences.FeedCacheIdleTimeoutSeconds']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @Range(min = 1l, max = 864000)
    @NotNull
    private Long feedCacheIdleTimeoutSeconds;

    public Long getConnectionTimeoutSeconds() {
        return connectionTimeoutSeconds;
    }

    public Long getFeedCacheUpdateTimeoutSeconds() {
        return feedCacheUpdateTimeoutSeconds;
    }

    public Long getFeedCacheIdleTimeoutSeconds() {
        return feedCacheIdleTimeoutSeconds;
    }
}
