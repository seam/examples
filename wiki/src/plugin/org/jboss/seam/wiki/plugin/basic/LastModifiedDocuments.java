/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiNode;

import java.io.Serializable;
import java.util.List;

@Name("lastModifiedDocuments")
@Scope(ScopeType.PAGE)
public class LastModifiedDocuments implements Serializable {

    public List<WikiDocument> getListOfDocuments(LastModifiedDocumentsPreferences prefs) {
        return
            WikiNodeDAO.instance().findWikiDocuments(
                Long.valueOf(prefs.getNumberOfItems()).intValue(),
                WikiNode.SortableProperty.lastModifiedOn,
                false
            );
    }

}
