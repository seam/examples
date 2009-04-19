package org.jboss.seam.wiki.core.preferences.editor;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.PreferenceValueTemplate;
import org.hibernate.validator.NotNull;

import java.util.List;
import java.io.Serializable;

@Name("editorSelectOne")
@Scope(ScopeType.CONVERSATION)
public class EditorSelectOne implements Serializable {

    public List<String> getAllValues(PreferenceEntity.Property property) {
        if (property.getTemplateComponentName() == null || property.getTemplateComponentName().length() == 0)
            throw new RuntimeException("No value template component name " + property);

        PreferenceValueTemplate template =
            (PreferenceValueTemplate)Component.getInstance(property.getTemplateComponentName());

        if (template == null)
            throw new RuntimeException("Couldn't find template component name: " + property.getTemplateComponentName());

        return template.getTemplateValues();

    }

    public boolean isNullable(PreferenceEntity.Property property) {
        return !property.getField().isAnnotationPresent(NotNull.class);
    }
}