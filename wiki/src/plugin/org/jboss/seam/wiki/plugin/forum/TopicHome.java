package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiTextMacro;
import org.jboss.seam.wiki.core.ui.WikiRedirect;
import org.jboss.seam.wiki.core.plugin.PluginRegistry;
import org.jboss.seam.wiki.preferences.Preferences;

import static org.jboss.seam.international.StatusMessage.Severity.INFO;

@Name("topicHome")
@Scope(ScopeType.CONVERSATION)
public class TopicHome extends DocumentHome {

    public static final String TOPIC_LIST_MACRO             = "forumTopics";
    public static final String TOPIC_LIST_MACRO_ML_PARAM    = "mailingList";
    public static final String TOPIC_NOTIFY_ME_MACRO        = "forumNotifyReplies";
    public static final String TOPIC_NOTIFY_LIST_TEMPLATE   = "/mailtemplates/forumNotifyTopicToList.xhtml";

    @In
    WikiDirectory currentDirectory;

    @In
    WikiDocument currentDocument;

    private boolean showForm = false;
    private boolean sticky = false;
    private boolean notifyReplies = false;
    private String mailingList;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected boolean isPageRootController() {
        return false;
    }

    @Override
    public Class<WikiDocument> getEntityClass() {
        return WikiDocument.class;
    }

    @Override
    public void create() {
        super.create();
        setParentNodeId(currentDirectory.getId());

        Boolean preferencesNotifyReplies = Preferences.instance().get(ForumPreferences.class).getNotifyMeOfReplies();
        notifyReplies = preferencesNotifyReplies != null && preferencesNotifyReplies;

        // TODO: mailingList = getMailingListFromTopicMacro();

        textEditor.setKey("topic");
        textEditor.setAllowPlaintext(true);// Topics can be plain text, regular documents can't
    }

    @Override
    public WikiDocument afterNodeCreated(WikiDocument doc) {
        WikiDocument newTopic = super.afterNodeCreated(doc);
        newTopic.setDefaults(new TopicDefaults());
        setPushOnFeeds(true);
        return newTopic;
    }

    @Override
    protected boolean beforePersist() {
        // TODO: Use macro parameters for "sticky" and "notify" options instead of additional macros
        if (isSticky()) {
            getInstance().removeHeaderMacros("forumPosting");
            getInstance().addHeaderMacro(new WikiTextMacro("forumStickyPosting"));
        }
        if (isNotifyReplies()) {
            getInstance().addHeaderMacro(new WikiTextMacro(TOPIC_NOTIFY_ME_MACRO));
        }
        return super.beforePersist();
    }

    @Override
    public String persist() {
        // Only owners or admins can edit topics
        getInstance().setWriteAccessLevel(org.jboss.seam.wiki.core.model.Role.ADMINROLE_ACCESSLEVEL);

        String outcome = super.persist();
        if (outcome != null) {

            // Notify forum mailing list
            /* TODO
            if (mailingList != null) {
                getLog().debug("sending topic e-mail to list: " + mailingList);
                Renderer.instance().render(
                    PluginRegistry.instance().getPlugin("forum").getPackageThemePath()+TOPIC_NOTIFY_LIST_TEMPLATE
                );
            }
            */
            mailingList =
                    Preferences.instance().get(ForumPreferences.class).getNotificationMailingList();
            if (mailingList != null) {
                getLog().debug("sending topic notification e-mail to forum list: " + mailingList);
                Renderer.instance().render(
                    PluginRegistry.instance().getPlugin("forum").getPackageThemePath()+TOPIC_NOTIFY_LIST_TEMPLATE
                );
            }

            endConversation();

            // Redirect to topic (so the created message is actually never displayed, but that's ok)
            WikiRedirect.instance().setWikiDocument(getInstance()).execute();
        }
        return null; // Prevent navigation
    }

    @Override
    public String update() {
        String outcome = super.update();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    @Override
    public String remove() {
        String outcome = super.remove();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Topic.Persist",
                "Topic '{0}' has been saved.",
                getInstance().getName()
        );
    }

    protected void updatedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Topic.Update",
                "Topic '{0}' has been updated.",
                getInstance().getName()
        );
    }

    protected void deletedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "forum.msg.Topic.Delete",
                "Topic '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    protected String getEditorWorkspaceDescription(boolean create) {
        return null;
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void endConversation() {
        getLog().debug("ending conversation and hiding topic form");
        showForm = false;
        Conversation.instance().end();
        getEntityManager().clear(); // Need to force re-read in the topic list refresh
        Events.instance().raiseEvent("Forum.topicListRefresh");
    }

    protected String getFeedEntryManagerName() {
        return "forumTopicFeedEntryManager";
    }

    public String getMailingListFromTopicMacro() {
        for (WikiTextMacro macro : currentDocument.getContentMacros()) {
            if (macro.getName().equals(TOPIC_LIST_MACRO))
                return macro.getParamValue(TOPIC_LIST_MACRO_ML_PARAM);
        }
        return null;
    }

    /* -------------------------- Public Features ------------------------------ */

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isNotifyReplies() {
        return notifyReplies;
    }

    public void setNotifyReplies(boolean notifyReplies) {
        this.notifyReplies = notifyReplies;
    }

    public String getMailingList() {
        return mailingList;
    }

    public String getMailingListName() {
        return currentDocument.getName();
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void newTopic() {
        initEditor(false);
        showForm = true;
    }

    public void cancel() {
        endConversation();

        // Redirect to topic list
        WikiRedirect.instance().setWikiDocument(currentDocument).execute();
    }

    @RequestParameter("showTopicForm")
    public void showTopicForm(Boolean requestParam) {
        if (requestParam != null && requestParam && !showForm) {
            getLog().debug("request parameter sets topic form visible, starts conversation");
            Conversation.instance().begin(true, false);
            Conversation.instance().changeFlushMode(FlushModeType.MANUAL);
            newTopic();
        }
    }

}
