/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.international.Messages;
import org.jboss.seam.wiki.core.model.WikiDocumentDefaults;
import org.jboss.seam.wiki.core.model.WikiDocument;

/**
 * @author Christian Bauer
 */
public class TopicDefaults extends WikiDocumentDefaults {

    public TopicDefaults() {
        super();
    }

    @Override
    public String getName() {
        return Messages.instance().get("forum.label.NewTopic");
    }

    @Override
    public String[] getHeaderMacrosAsString() {
        return new String[] { "clearBackground", "hideControls", "hideComments",
                              "hideTags", "hideCreatorHistory", "disableContentMacros", "forumPosting" };
    }

    @Override
    public String getContentText() {
        return Messages.instance().get("lacewiki.msg.wikiTextEditor.EditThisText");
    }

    @Override
    public String[] getFooterMacrosAsString() {
        return new String[] { "forumReplies" };
    }

    @Override
    public void setOptions(WikiDocument newTopic) {
        newTopic.setNameAsTitle(false);
        newTopic.setEnableComments(true);
        newTopic.setEnableCommentForm(true);
        newTopic.setEnableCommentsOnFeeds(true);
    }
}
