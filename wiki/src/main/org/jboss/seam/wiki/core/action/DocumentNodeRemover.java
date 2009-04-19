/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.Component;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("documentNodeRemover")
public class DocumentNodeRemover extends NodeRemover<WikiDocument> implements Serializable {

    @In
    CommentNodeRemover commentNodeRemover;

    public boolean isRemovable(WikiDocument doc) {
        WikiNode wikiStart = (WikiNode)Component.getInstance("wikiStart");
        return !doc.getId().equals(wikiStart.getId());
    }

    public void trash(WikiDocument doc) {

        feedDAO.removeFeedEntry(
            feedDAO.findFeeds(doc),
            feedDAO.findFeedEntry(doc)
        );

        List<WikiNode> children = getWikiNodeDAO().findChildren(doc, WikiNode.SortableProperty.createdOn, false, 0, Integer.MAX_VALUE);
        for (WikiNode child : children) {
            if (child.isInstance(WikiComment.class)) {
                getLog().debug("deleting dependent comment: " + child);
                commentNodeRemover.trash( (WikiComment)child );
            }
        }

        super.trash(doc);
    }

    public void removeDependencies(WikiDocument doc) {
        getLog().debug("removing dependencies of: " + doc);

        List<WikiNode> children = getWikiNodeDAO().findChildren(doc, WikiNode.SortableProperty.createdOn, false, 0, Integer.MAX_VALUE);
        for (WikiNode child : children) {
            if (child.isInstance(WikiComment.class)) {
                getLog().debug("deleting dependent comment: " + child);
                commentNodeRemover.removeDependencies( (WikiComment)child );
            }
            getEntityManager().remove(child);
        }

    }
}