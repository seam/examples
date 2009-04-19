package org.jboss.seam.wiki.plugin.faqBrowser;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.model.WikiDocument;

import static org.jboss.seam.international.StatusMessage.Severity.INFO;


@Name("faqQuestionHome")
@Scope(ScopeType.CONVERSATION)
public class FaqQuestionHome extends DocumentHome {

    @In(create = true)
    FaqBrowser faqBrowser;

    private boolean showForm = false;

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
        setParentNodeId(faqBrowser.getSelectedDir().getWrappedNode().getId());
    }

    @Override
    public WikiDocument afterNodeCreated(WikiDocument doc) {
        WikiDocument newQuestion = super.afterNodeCreated(doc);
        newQuestion.setDefaults(new FaqQuestionDefaults());
        return newQuestion;
    }

    @Override
    public String persist() {
        String outcome = super.persist();
        if (outcome != null) endConversation();
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
                "fb.faqBrowser.msg.Question.Persist",
                "Question '{0}' has been saved.",
                getInstance().getName()
        );
    }

    protected void updatedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "fb.faqBrowser.msg.Question.Update",
                "Question '{0}' has been updated.",
                getInstance().getName()
        );
    }

    protected void deletedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "fb.faqBrowser.msg.Question.Delete",
                "Question '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void endConversation() {
        getLog().debug("ending conversation and hiding question form");
        showForm = false;
        Conversation.instance().end();
        getEntityManager().clear(); // Need to force re-read in the question list refresh
        Events.instance().raiseEvent("FaqBrowser.questionListRefresh");
    }

    /* -------------------------- Public Features ------------------------------ */

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void newQuestion() {
        initEditor(false);
        showForm = true;
    }

    public void cancel() {
        endConversation();
    }

}