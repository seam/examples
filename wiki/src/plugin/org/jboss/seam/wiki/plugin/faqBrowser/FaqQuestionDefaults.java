/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.faqBrowser;

import org.jboss.seam.wiki.core.model.WikiDocumentDefaults;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.template.WikiDocumentTemplate;
import org.jboss.seam.international.Messages;

/**
 * @author Christian Bauer
 */
public class FaqQuestionDefaults extends WikiDocumentDefaults {

    public FaqQuestionDefaults() {
        super();
    }

    @Override
    public String getName() {
        return Messages.instance().get("fb.faqBrowser.label.NewQuestionTitle");
    }

    @Override
    public String[] getHeaderMacrosAsString() {
        return new String[]{"faqBrowser", "docPager"};
    }

    @Override
    public String getContentText() {
        return Messages.instance().get("lacewiki.msg.wikiTextEditor.EditThisText");
    }

    @Override
    public void setOptions(WikiDocument newQuestion) {
        newQuestion.setNameAsTitle(true);
        newQuestion.setEnableComments(true);
        newQuestion.setEnableCommentForm(true);
        newQuestion.setEnableCommentsOnFeeds(false);
    }

}
