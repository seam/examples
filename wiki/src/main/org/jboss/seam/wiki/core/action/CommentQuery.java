/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.action.prefs.CommentsPreferences;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Christian Bauer
 */
@Name("commentQuery")
@Scope(ScopeType.CONVERSATION)
public class CommentQuery implements Serializable {

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    protected DocumentHome documentHome;

    @In("#{preferences.get('Comments')}")
    protected CommentsPreferences commentsPreferences;

    protected List<WikiComment> comments;

    public List<WikiComment> getComments() {
        if (comments == null) loadComments();
        return comments;
    }

    @Observer(value = {
            "PreferenceComponent.refresh.commentsPreferences",
            "Comment.commentListRefresh"
            }, create = false)
    public void loadComments() {

        // Don't do the expensive query if the simple query doesn't return children
        if (wikiNodeDAO.findChildrenCount(documentHome.getInstance()) == 0) {
            comments = new ArrayList<WikiComment>();
            return;
        }

        if (commentsPreferences.getThreaded()) {
            comments = wikiNodeDAO.findWikiCommentsThreaded(documentHome.getInstance());
        } else {
            comments = wikiNodeDAO.findWikiCommentsFlat(documentHome.getInstance(), commentsPreferences.getListAscending());
        }
    }

}
