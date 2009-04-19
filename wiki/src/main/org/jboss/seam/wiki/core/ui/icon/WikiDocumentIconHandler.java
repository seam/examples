/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui.icon;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.action.DirectoryBrowser;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("wikiDocumentIconHandler")
public class WikiDocumentIconHandler extends IconHandler<WikiDocument> implements Serializable {

    @In
    DirectoryBrowser directoryBrowser;

    public String getIconName(WikiDocument doc) {
        if (directoryBrowser.getInstance().getDefaultFile() != null &&
            directoryBrowser.getInstance().getDefaultFile().getId().equals(doc.getId())) return "icon.doc.default.gif";
        return "icon.doc.gif";
    }
    
}