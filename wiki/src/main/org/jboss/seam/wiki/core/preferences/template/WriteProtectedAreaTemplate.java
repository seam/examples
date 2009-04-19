package org.jboss.seam.wiki.core.preferences.template;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.preferences.PreferenceValueTemplate;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

@Name("writeProtectedAreaPreferenceValueTemplate")
@Scope(ScopeType.CONVERSATION)
public class WriteProtectedAreaTemplate implements PreferenceValueTemplate, Serializable {

    @In
    WikiDirectory wikiRoot;

    private List<String> areaNames;

    public List<String> getTemplateValues() {
        if (areaNames == null) {
            areaNames = new ArrayList<String>();
            List<WikiNode> areas =
                WikiNodeDAO.instance().findChildren(wikiRoot, WikiNode.SortableProperty.name, false, 0, Integer.MAX_VALUE);
            for (WikiNode area : areas) {
                if (area.isWriteProtected()) {
                    areaNames.add(area.getName());
                }
            }
        }
        return areaNames;
    }

}