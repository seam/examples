package org.jboss.seam.wiki.core.preferences.editor;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.hibernate.validator.Length;

import java.io.Serializable;

@Name("editorAdaptiveTextInput")
@Scope(ScopeType.CONVERSATION)
public class EditorAdaptiveTextInput implements Serializable {

    public static final Long DEFAULT_TEXTINPUT_LENGTH = 25l;
    public static final Long DEFAULT_TEAXTAREA_LENGTH = 255l;
    public static final Long DEFAULT_TEAXTAREA_COLS = 25l;
    public static final Long DEFAULT_TEAXTAREA_ROWS = 10l;

    public long getSize(PreferenceEntity.Property property) {
        return getMaxLength(property) <= DEFAULT_TEXTINPUT_LENGTH ? getMaxLength(property) : DEFAULT_TEXTINPUT_LENGTH;
    }

    public long getMaxLength(PreferenceEntity.Property property) {
        checkAnnotation(property);
        return property.getField().getAnnotation(Length.class).max();
    }

    public boolean isRenderTextArea(PreferenceEntity.Property property) {
        return getMaxLength(property) > DEFAULT_TEAXTAREA_LENGTH;
    }

    public long getTextAreaCols(PreferenceEntity.Property property) {
        return DEFAULT_TEAXTAREA_COLS;
    }

    public long getTextAreaRows(PreferenceEntity.Property property) {
        return DEFAULT_TEAXTAREA_ROWS;
    }

    private void checkAnnotation(PreferenceEntity.Property property) {
        if (!property.getField().isAnnotationPresent(Length.class)) {
            throw new IllegalArgumentException("property does not have @Length annotation " + property);
        }
    }

}
