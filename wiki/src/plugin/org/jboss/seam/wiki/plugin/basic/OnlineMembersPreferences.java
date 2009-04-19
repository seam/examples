/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.hibernate.validator.Range;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;

import java.io.Serializable;

@Preferences(
    name = "OnlineMembers",
    description = "#{messages['basic.onlineMembers.preferences.description']}",
    mappedTo = "basic.onlineMembers"
)
public class OnlineMembersPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['basic.onlineMembers.preferences.property.membersPerRow']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 50l)
    private Long membersPerRow;

    public Long getMembersPerRow() {
        return membersPerRow;
    }
}