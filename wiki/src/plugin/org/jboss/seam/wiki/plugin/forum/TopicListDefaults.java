/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.international.Messages;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiDocumentDefaults;

/**
 * @author Christian Bauer
 */
public class TopicListDefaults extends WikiDocumentDefaults {

    public TopicListDefaults(WikiDocument template) {
        super(template);
    }

    @Override
    public String getName() {
        return getTemplate().getName() + " " + Messages.instance().get("forum.label.Forum");
    }

    @Override
    public String[] getHeaderMacrosAsString() {
        return new String[]{"clearBackground", "hideControls", "hideComments", "hideTags", "hideCreatorHistory"};
    }

    @Override
    public String[] getContentMacrosAsString() {
        return new String[]{"forumTopics"};
    }

    @Override
    public String getContentText() {
        return "";
    }

    @Override
    public void setOptions(WikiDocument document) {
        document.setAreaNumber(getTemplate().getAreaNumber());
        document.setWikiname(WikiUtil.convertToWikiName(document.getName()));
        document.setNameAsTitle(true);
        document.setReadAccessLevel(getTemplate().getReadAccessLevel());
        document.setWriteAccessLevel(org.jboss.seam.wiki.core.model.Role.ADMINROLE_ACCESSLEVEL);
        document.setEnableComments(false);
        document.setEnableCommentForm(false);
        document.setEnableCommentsOnFeeds(false);
        document.setCreatedBy(getTemplate().getCreatedBy());
    }
}
