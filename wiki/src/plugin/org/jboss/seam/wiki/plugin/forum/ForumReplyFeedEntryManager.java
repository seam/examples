package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.feeds.WikiCommentFeedEntryManager;
import org.jboss.seam.wiki.core.model.WikiComment;

@Name("forumReplyFeedEntryManager")
public class ForumReplyFeedEntryManager extends WikiCommentFeedEntryManager {

    public String getFeedEntryTitle(WikiComment comment) {
        return "[" + comment.getParentDocument().getParent().getName() + "] " + comment.getSubject();
    }

}
