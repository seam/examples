package org.jboss.seam.wiki.core.action.prefs;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;

import java.io.Serializable;

@Preferences(name = "DocEditor", description = "#{messages['lacewiki.preferences.documentEditor.Name']}")
public class DocumentEditorPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.documentEditor.MinorRevisionEnabled']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.USER}
    )
    @NotNull
    private Boolean minorRevisionEnabled;

    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.documentEditor.RegularEditAreaRows']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.USER},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 5l, max = 100l)
    @NotNull
    private Long regularEditAreaRows;

    // TODO: This property is not used anymore - or it shouldn't be used...
    @PreferenceProperty(
        description = "#{messages['lacewiki.preferences.documentEditor.RegularEditAreaColumns']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.USER},
        editorIncludeName = "NumberRange"

    )
    @Range(min = 5l, max = 250l)
    @NotNull
    private Long regularEditAreaColumns;

    public Boolean getMinorRevisionEnabled() {
        return minorRevisionEnabled;
    }

    public Long getRegularEditAreaRows() {
        return regularEditAreaRows;
    }

    public Long getRegularEditAreaColumns() {
        return regularEditAreaColumns;
    }
}
