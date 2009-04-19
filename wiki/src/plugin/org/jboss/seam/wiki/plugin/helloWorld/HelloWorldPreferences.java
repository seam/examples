/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.helloWorld;

import org.hibernate.validator.Length;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;

/**
 * @author Christian Bauer
 */
@Preferences(
    name = "HelloWorld",
    description = "#{messages['hw.preferences.Description']}",
    mappedTo = "hw.helloWorld"
)
public class HelloWorldPreferences {

    @PreferenceProperty(
        description = "#{messages['hw.preferences.property.Message']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String message;

    public String getMessage() {
        return message;
    }
}
