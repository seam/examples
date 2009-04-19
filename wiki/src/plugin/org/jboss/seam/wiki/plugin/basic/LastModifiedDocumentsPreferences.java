package org.jboss.seam.wiki.plugin.basic;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.hibernate.validator.Length;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;

import java.io.Serializable;

@Preferences(
    name = "LastModifiedDocuments",
    description = "#{messages['basic.lastModifiedDocuments.preferences.description']}",
    mappedTo = "basic.lastModifiedDocuments"
)
public class LastModifiedDocumentsPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['basic.lastModifiedDocuments.preferences.property.title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['basic.lastModifiedDocuments.preferences.property.numberOfItems']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 25l)
    @NotNull
    private Long numberOfItems;

    @PreferenceProperty(
        description = "#{messages['basic.lastModifiedDocuments.preferences.property.showUsernames']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE}
    )
    @NotNull
    private Boolean showUsernames;

    @PreferenceProperty(
        description = "#{messages['basic.lastModifiedDocuments.preferences.property.documentTitleLength']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 5l, max = 100l)
    @NotNull
    private Long documentTitleLength;

    public String getTitle() {
        return title;
    }

    public Long getNumberOfItems() {
        return numberOfItems;
    }

    public Boolean getShowUsernames() {
        return showUsernames;
    }

    public Long getDocumentTitleLength() {
        return documentTitleLength;
    }
}
