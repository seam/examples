/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Length;
import org.hibernate.validator.Email;

/**
 * @author Christian Bauer
 */
@Preferences(
    name = "ForumTopics",
    description = "#{messages['forum.topics.preferences.description']}",
    mappedTo = "forum.topics"
)
public class ForumTopicsPreferences {

    @PreferenceProperty(
        description = "#{messages['forum.topics.preferences.property.mailingList']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 4, max = 255)
    @Email
    private String mailingList;

    public String getMailingList() {
        return mailingList;
    }
}
