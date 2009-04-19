package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.annotations.*;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.action.CommentHome;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.ui.WikiRedirect;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.preferences.Preferences;

import static org.jboss.seam.international.StatusMessage.Severity.INFO;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Name("replyHome")
@Scope(ScopeType.CONVERSATION)
public class ReplyHome extends CommentHome {

    public static final String REPLY_NOTIFY_ORIGINAL_POSTER_TEMPLATE    = "/mailtemplates/forumNotifyReply.xhtml";
    public static final String REPLY_NOTIFY_LIST_TEMPLATE               = "/mailtemplates/forumNotifyReplyToList.xhtml";
    public static final String REPLY_NOTIFY_POSTERS_TEMPLATE            = "/mailtemplates/forumNotifyReplyToPosters.xhtml";

    @Override
    public void create() {
        super.create();
        markTopicRead();

        textEditor.setKey("reply");
    }

    @In(create = true)
    private Renderer renderer;

    // Triggered by superclass after persist() method completes
    @Observer(value = "Comment.persisted", create = false)
    public void sendNotificationMails() {

        // Notify forum mailing list
        String notificationMailingList =
                Preferences.instance().get(ForumPreferences.class).getNotificationMailingList();
        if (notificationMailingList != null) {
            getLog().debug("sending reply notification e-mail to forum list: " + notificationMailingList);
            renderer.render(PluginRegistry.instance().getPlugin("forum").getPackageThemePath()+REPLY_NOTIFY_LIST_TEMPLATE);
        }

        // Notify original poster (unless it is the user who posted the comment)
        if (documentHome.getInstance().macroPresent(TopicHome.TOPIC_NOTIFY_ME_MACRO)
            && !documentHome.getInstance().getCreatedBy().getId().equals(getInstance().getCreatedBy().getId())
           ) {
            getLog().debug("sending reply notification e-mail to original poster of topic");
            renderer.render(PluginRegistry.instance().getPlugin("forum").getPackageThemePath()+ REPLY_NOTIFY_ORIGINAL_POSTER_TEMPLATE);
        }

        // Find all posters of the thread
        Set<User> notifyPosters = new HashSet();
        getLog().debug("finding all posters of current topic thread");
        List<WikiComment> comments = getWikiNodeDAO().findWikiCommentsFlat(documentHome.getInstance(), true);
        for (WikiComment comment : comments) {

            Long commentPosterId = comment.getCreatedBy().getId();
            // Notify the guy if he is not a) the poster of the current comment or b) the original topic poster
            if (!commentPosterId.equals(getInstance().getCreatedBy().getId()) &&
                !commentPosterId.equals(documentHome.getInstance().getCreatedBy().getId())) {

                getLog().debug("adding poster to notification list: " + comment.getCreatedBy());
                notifyPosters.add(comment.getCreatedBy()); // The set filters duplicate user instances
            }
        }

        // Send them an e-mail as well if preferences option is enabled
        for (User poster : notifyPosters) {

            // Reading this users preferences is a bit awkward...
            Contexts.getEventContext().set("currentPreferencesUser", poster);
            Boolean preferencesNotifyReplies = Preferences.instance().get(ForumPreferences.class).getNotifyMeOfReplies();
            boolean notifyReplies = preferencesNotifyReplies != null && preferencesNotifyReplies;
            Contexts.getEventContext().remove("currentPreferencesUser");

            if (notifyReplies) {
                getLog().debug("sending reply notification e-mail to poster on the thread: " + poster);
                Contexts.getEventContext().set("notifyPoster", poster);
                renderer.render(PluginRegistry.instance().getPlugin("forum").getPackageThemePath()+ REPLY_NOTIFY_POSTERS_TEMPLATE);
            } else {
                getLog().debug("notification not enabled for poster: " + poster);
            }
        }

    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void replyToDocument() {

        getLog().debug("reply to document id: " + getParentNodeId());
        newComment();
        initEditor(false);

        getInstance().setSubject(REPLY_PREFIX + getParentNode().getName());

        textEditor.setValue(getInstance().getContent());

        WikiRedirect.instance()
                .setWikiDocument(documentHome.getInstance())
                .setPropagateConversation(true)
                .execute();
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void quoteDocument() {

        getLog().debug("quote to document id: " + getParentNodeId());
        newComment();
        initEditor(false);

        getInstance().setSubject(REPLY_PREFIX + getParentNode().getName());

        getInstance().setContent(quote(
            documentHome.getInstance().getContent(),
            documentHome.getInstance().getCreatedOn(),
            documentHome.getInstance().getCreatedBy().getFullname()
        ));

        textEditor.setValue(getInstance().getContent());

        WikiRedirect.instance()
                .setWikiDocument(documentHome.getInstance())
                .setPropagateConversation(true)
                .execute();
    }

    @Override
    public boolean isPersistAllowed(WikiComment node, WikiNode parent) {
        /* Forum replies require write permissions on the forum directory */
        Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
        return Identity.instance().hasPermission("Comment", "create", documentHome.getInstance())
                && (documentHome.getParentNode().getWriteAccessLevel() <= currentAccessLevel);
    }

    protected String getFeedEntryManagerName() {
        return "forumReplyFeedEntryManager";
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Reply.Persist",
                "Reply '{0}' has been saved.",
                getInstance().getSubject()
        );
    }

    protected void updatedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Reply.Update",
                "Reply '{0}' has been updated.",
                getInstance().getSubject()
        );
    }

    protected void deletedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Reply.Delete",
                "Reply '{0}' has been deleted.",
                getInstance().getSubject()
        );
    }

    private void markTopicRead() {
        if (!getCurrentUser().isAdmin() && !getCurrentUser().isGuest()) {
            getLog().debug("adding to read topics, forum id: "
                            + documentHome.getParentNode().getId() + " topic id: " + documentHome.getInstance().getId());
            ForumTopicReadManager forumTopicReadManager = (ForumTopicReadManager)Component.getInstance(ForumTopicReadManager.class);
            forumTopicReadManager.addTopicId(documentHome.getParentNode().getId(), documentHome.getInstance().getId());
        }
    }

}
