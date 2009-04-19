package org.jboss.seam.wiki.core.preferences.editor;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("editorNumberRange")
@Scope(ScopeType.CONVERSATION)
public class EditorNumberRange implements Serializable {

    public long getRangeMin(PreferenceEntity.Property property) {
        checkAnnotation(property);
        return property.getField().getAnnotation(Range.class).min();
    }

    public long getRangeMax(PreferenceEntity.Property property) {
        checkAnnotation(property);
        return property.getField().getAnnotation(Range.class).max();
    }

    private void checkAnnotation(PreferenceEntity.Property property) {
        if (!property.getField().isAnnotationPresent(Range.class)) {
            throw new IllegalArgumentException("property does not have @Range annotation " + property);
        }
    }
}
