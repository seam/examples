package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.framework.EntityNotFoundException;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.util.WikiUtil;

import java.io.Serializable;

@Name("help")
@Scope(ScopeType.SESSION)
public class Help implements Serializable {

    @Logger
    Log log;

    @In("#{preferences.get('Wiki')}")
    WikiPreferences wikiPreferences;

    WikiDirectory helpAreaRoot;

    @Create
    public void create() {
        helpAreaRoot = WikiNodeDAO.instance().findArea(WikiUtil.convertToWikiName(wikiPreferences.getHelpArea()));
    }

    WikiDocument selectedHelpDoc;

    public WikiDocument getSelectedHelpDoc() {
        return selectedHelpDoc;
    }

    public void selectDocumentByName(String documentName) {
        log.debug("Searching for help document with wiki name in area: " + helpAreaRoot.getAreaNumber() + ", " + WikiUtil.convertToWikiName(documentName));
        selectedHelpDoc =
                WikiNodeDAO.instance().findWikiDocumentInArea(
                        helpAreaRoot.getAreaNumber(),
                        WikiUtil.convertToWikiName(documentName)
                );
        if (selectedHelpDoc == null)
            throw new EntityNotFoundException("Help document: "+documentName, WikiDocument.class);

        log.debug("Found help document: " + selectedHelpDoc);
    }

}
