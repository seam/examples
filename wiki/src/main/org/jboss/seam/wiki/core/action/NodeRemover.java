/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * @author Christian Bauer
 */
@Scope(ScopeType.CONVERSATION)
public abstract class NodeRemover<N extends WikiNode> {

    @Logger
    Log log;

    @In
    protected FeedDAO feedDAO;

    @In
    protected WikiNodeDAO wikiNodeDAO;

    @In
    protected UserDAO userDAO;

    @In
    protected EntityManager restrictedEntityManager;

    @In
    protected WikiDirectory trashArea;

    public abstract boolean isRemovable(N node);

    public void trash(N node) {

        // Check if the cut item was a default file for its parent
        if (node.getParent().isInstance(WikiDirectory.class)) {
            WikiDirectory parent = (WikiDirectory)node.getParent();
            if ( parent.getDefaultFile() != null && parent.getDefaultFile().getId().equals(node.getId())) {
                getLog().debug("trashing default file of directory: " + parent);
                parent.setDefaultFile(null);
            }
        }

        // Generate a new (hopefully unique) name
        String newName = node.getName() + "." + new Date().getTime(); // Just append a timestamp

        // Add to trash area
        WikiDirectory persistentTrashArea = getEntityManager().find(WikiDirectory.class, trashArea.getId());
        node.setParent(persistentTrashArea);
        node.setAreaNumber(persistentTrashArea.getAreaNumber());
        node.setName(newName);
        node.setWikiname(WikiUtil.convertToWikiName(node.getName()));

    }

    public abstract void removeDependencies(N node);

    protected Log getLog() {
        return log;
    }

    protected FeedDAO getFeedDAO() {
        return feedDAO;
    }

    protected WikiNodeDAO getWikiNodeDAO() {
        return wikiNodeDAO;
    }

    protected UserDAO getUserDAO() {
        return userDAO;
    }

    protected  EntityManager getEntityManager() {
        return restrictedEntityManager;
    }
}
