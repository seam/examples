/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiMenuItem;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.Component;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("directoryNodeRemover")
public class DirectoryNodeRemover extends NodeRemover<WikiDirectory> implements Serializable {

    public boolean isRemovable(WikiDirectory dir) {
        if (getWikiNodeDAO().findChildrenCount(dir) > 0) {
            getLog().debug("directory is not deletable, has children: " + dir);
            return false;
        }

        WikiDirectory trashArea = (WikiDirectory)Component.getInstance("trashArea");
        WikiDirectory memberArea = (WikiDirectory)Component.getInstance("memberArea");
        WikiDirectory helpArea = (WikiDirectory) Component.getInstance("helpArea");

        return dir.getParent() != null // Wiki ROOT not deleteable
                && !dir.getId().equals(trashArea.getId())
                && !dir.getId().equals(memberArea.getId())
                && !dir.getId().equals(helpArea.getId());

    }

    public void trash(WikiDirectory node) {
        throw new IllegalStateException("Can't trash WikiDirectory instance");
    }

    public void removeDependencies(WikiDirectory dir) {
        getLog().debug("removing dependencies of: " + dir);

        // Feed
        if (dir.getFeed() != null) {
            getLog().debug("removing feed of directory: " + dir);
            getFeedDAO().removeFeed(dir);
        }

        // WikiMenuItem
        WikiMenuItem menuItem = getWikiNodeDAO().findMenuItem(dir);
        if (menuItem != null) {
            getLog().debug("removing menu item of directory: " + menuItem);
            getEntityManager().remove(menuItem);
        }

        // User.homeDirectory
        User user = getWikiNodeDAO().findWikiDirectoryMemberHome(dir.getId());
        if (user != null) {
            getLog().debug("setting member home to null: " + user);
            user.setMemberHome(null);
        }

    }
}
