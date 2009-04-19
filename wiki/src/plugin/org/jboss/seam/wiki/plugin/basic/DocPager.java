/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.Preferences;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("docPager")
@Scope(ScopeType.PAGE)
public class DocPager implements Serializable {

    public static final String MACRO_ATTR_PAGER_PREVIOUS = "docPagerPrevious";
    public static final String MACRO_ATTR_PAGER_NEXT    = "docPagerNext";

    @In
    WikiDocument currentDocument;

    public WikiDocument getPrevious(WikiPluginMacro macro) {
        WikiDocument previous = (WikiDocument) macro.getAttributes().get(MACRO_ATTR_PAGER_PREVIOUS);
        if (previous == null) {
            previous = WikiNodeDAO.instance().findSiblingWikiDocumentInDirectory(currentDocument, getSortingProperty(macro), true);
            macro.getAttributes().put(MACRO_ATTR_PAGER_PREVIOUS, previous);
        }
        return previous;
    }

    public WikiDocument getNext(WikiPluginMacro macro) {
        WikiDocument next = (WikiDocument) macro.getAttributes().get(MACRO_ATTR_PAGER_NEXT);
        if (next == null) {
            next = WikiNodeDAO.instance().findSiblingWikiDocumentInDirectory(currentDocument, getSortingProperty(macro), false);
            macro.getAttributes().put(MACRO_ATTR_PAGER_NEXT, next);
        }
        return next;
    }

    private WikiNode.SortableProperty getSortingProperty(WikiPluginMacro macro) {
        DocPagerPreferences prefs = Preferences.instance().get(DocPagerPreferences.class, macro);
        // By default, previous/next documents are searched by creation date
        WikiNode.SortableProperty byProperty = WikiNode.SortableProperty.createdOn;
        if (prefs.getByProperty() != null) {
            try {
                byProperty = WikiNode.SortableProperty.valueOf(prefs.getByProperty());
            } catch (IllegalArgumentException ex) {}
        }
        return byProperty;
    }

}