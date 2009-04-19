/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Length;

/**
 * @author Christian Bauer
 */
@Preferences(
    name = "DirToc",
    description = "#{messages['basic.dirToc.preferences.description']}",
    mappedTo = "basic.dirToc"
)
public class DirTocPreferences {

    @PreferenceProperty(
        description = "#{messages['basic.dirToc.preferences.property.showRootDocuments']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean showRootDocuments;

    @PreferenceProperty(
        description = "#{messages['basic.dirToc.preferences.property.showDefaultDocuments']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean showDefaultDocuments;

    @PreferenceProperty(
        description = "#{messages['basic.dirToc.preferences.property.showLastUpdatedTimestamp']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean showLastUpdatedTimestamp;

    @PreferenceProperty(
        description = "#{messages['dirToc.preferences.property.withHeaderMacro']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String withHeaderMacro;

    @PreferenceProperty(
        description = "#{messages['dirToc.preferences.property.rootDocumentLink']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String rootDocumentLink;

    public Boolean getShowRootDocuments() {
        return showRootDocuments;
    }

    public Boolean getShowDefaultDocuments() {
        return showDefaultDocuments;
    }

    public Boolean getShowLastUpdatedTimestamp() {
        return showLastUpdatedTimestamp;
    }

    public String getWithHeaderMacro() {
        return withHeaderMacro;
    }

    public String getRootDocumentLink() {
        return rootDocumentLink;
    }
}
