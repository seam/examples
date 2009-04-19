/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui.icon;

import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("wikiDirectoryIconHandler")
public class WikiDirectoryIconHandler extends IconHandler<WikiDirectory> implements Serializable {

    @In
    WikiDirectory trashArea;

    @In
    WikiDirectory memberArea;

    @In
    WikiDirectory helpArea;

    public String getIconName(WikiDirectory dir) {
        if (dir.getId().equals(trashArea.getId())) return "icon.trash.gif";
        if (dir.getId().equals(helpArea.getId())) return "icon.help.gif";
        if (dir.getId().equals(memberArea.getId())) return "icon.user.gif";
        return "icon.dir.gif";
    }
}
