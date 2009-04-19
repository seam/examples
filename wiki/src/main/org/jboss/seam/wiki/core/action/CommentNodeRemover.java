/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.WikiComment;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("commentNodeRemover")
@AutoCreate
public class CommentNodeRemover extends NodeRemover<WikiComment> implements Serializable {

    public boolean isRemovable(WikiComment comment) {
        return true;
    }

    public void trash(WikiComment comment) {
        feedDAO.removeFeedEntry(
            feedDAO.findFeeds(comment),
            feedDAO.findFeedEntry(comment)
        );
    }

    public void removeDependencies(WikiComment comment) {
        getLog().debug("removing dependencies of: " + comment);

        List<WikiComment> children = getWikiNodeDAO().findWikiCommentSubtree(comment, true);
        // These are ordered by level, deepest first, so no FK violation if we delete in that order
        // (does it matter? Hibernate should sort it...)
        for (WikiComment child : children) {
            getLog().debug("deleting child comment: " + child);
            feedDAO.removeFeedEntry(
                feedDAO.findFeeds(child),
                feedDAO.findFeedEntry(child)
            );
            getEntityManager().remove(child);
        }

        feedDAO.removeFeedEntry(
            feedDAO.findFeeds(comment),
            feedDAO.findFeedEntry(comment)
        );
    }
}