/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.feeds;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.international.Messages;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.model.WikiCommentFeedEntry;
import org.jboss.seam.wiki.core.model.User;

@Name("wikiCommentFeedEntryManager")
public class WikiCommentFeedEntryManager extends FeedEntryManager<WikiComment, WikiCommentFeedEntry> {

    @RaiseEvent("FeedEntry.created")
    public WikiCommentFeedEntry createFeedEntry(WikiComment comment) {

        WikiCommentFeedEntry fe = new WikiCommentFeedEntry();

        fe.setLink(wikiURLRenderer.renderURL(comment, true));
        fe.setTitle(getFeedEntryTitle(comment));
        fe.setAuthor(
            comment.getCreatedBy().getFullname() != null
            && !comment.getCreatedBy().getUsername().equals(User.GUEST_USERNAME)
            && !comment.getCreatedBy().getUsername().equals(User.ADMIN_USERNAME)
            ? comment.getCreatedBy().getFullname()
            : comment.getFromUserName());
        fe.setUpdatedDate(fe.getPublishedDate());

        // Do NOT use text/html, the fabulous Sun "Rome" software will
        // render type="HTML" (uppercase!) which kills the Firefox feed renderer!
        fe.setDescriptionType("html");
        fe.setDescriptionValue(getCommentDescription(comment));

        fe.setComment(comment);
        return fe;
    }

    @RaiseEvent("FeedEntry.updated")
    public void updateFeedEntry(WikiCommentFeedEntry fe, WikiComment comment) {

        fe.setLink(wikiURLRenderer.renderURL(comment, true));
        fe.setTitle(Messages.instance().get("lacewiki.label.comment.FeedEntryTitlePrefix") + " " + comment.getSubject());
        fe.setAuthor(comment.getCreatedBy().getFullname() != null ? comment.getCreatedBy().getFullname() : comment.getFromUserName());

        fe.setDescriptionValue(getCommentDescription(comment));
    }

    public String getFeedEntryTitle(WikiComment comment) {
        return Messages.instance().get("lacewiki.label.comment.FeedEntryTitlePrefix") + " " + comment.getSubject();
    }

    private String getCommentDescription(WikiComment comment) {
        StringBuilder desc = new StringBuilder();
        desc.append(Messages.instance().get("lacewiki.msg.comment.FeedIntro"));
        desc.append("&#160;");
        desc.append("<a href=\"").append(wikiURLRenderer.renderURL(comment.getParentDocument())).append("\">");
        desc.append("'").append(comment.getParentDocument().getName()).append("'");
        desc.append("</a>.");
        desc.append("<hr/>");
        desc.append(
            comment.isUseWikiText()
                ? renderWikiText(comment.getAreaNumber(), comment.getContent())
                : renderPlainText(comment.getContent())
        );
        return desc.toString();
    }

}
