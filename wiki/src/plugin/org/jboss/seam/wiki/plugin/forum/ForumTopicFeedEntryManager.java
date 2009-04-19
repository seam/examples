package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.feeds.WikiDocumentFeedEntryManager;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.annotations.Name;

@Name("forumTopicFeedEntryManager")
public class ForumTopicFeedEntryManager extends WikiDocumentFeedEntryManager {

    public String getFeedEntryTitle(WikiDocument document) {
        return "[" + document.getParent().getName() + "] " + document.getName();
    }
}
