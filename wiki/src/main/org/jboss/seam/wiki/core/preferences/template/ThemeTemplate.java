package org.jboss.seam.wiki.core.preferences.template;

import org.jboss.seam.wiki.preferences.PreferenceValueTemplate;

import java.util.List;
import java.io.Serializable;

public class ThemeTemplate implements PreferenceValueTemplate, Serializable {

    List<String> themes;

    public List<String> getTemplateValues() {
        return themes;
    }

    public void setTemplateValues(List<String> themes) {
        this.themes = themes;
    }

}
