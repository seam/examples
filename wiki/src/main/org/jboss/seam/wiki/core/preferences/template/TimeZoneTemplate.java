package org.jboss.seam.wiki.core.preferences.template;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.preferences.PreferenceValueTemplate;
import org.jboss.seam.ScopeType;

import java.util.TimeZone;
import java.util.Arrays;
import java.util.List;
import java.io.Serializable;

@Name("timeZonePreferenceValueTemplate")
@Scope(ScopeType.CONVERSATION)
public class TimeZoneTemplate implements PreferenceValueTemplate, Serializable {

    public List<String> getTemplateValues() {
        return Arrays.asList(TimeZone.getAvailableIDs());
    }

}
