/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.Log;
import org.jboss.seam.faces.RedirectException;
import org.jboss.seam.wiki.core.model.WikiDocument;

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("wikiRedirect")
@Scope(ScopeType.CONVERSATION)
public class WikiRedirect implements Serializable {

    @Logger
    Log log;

    private WikiDocument wikiDocument;
    private boolean forcePermURL = false;
    private String fragment;
    private boolean propagateConversation = false;

    /* TODO: That would be nice, we should add these when !forcePermURL
    private String year;
    private String month;
    private String day;
    private String page;
    private String tag;
    private String category;
    */

    public WikiDocument getWikiDocument() {
        return wikiDocument;
    }

    public WikiRedirect setWikiDocument(WikiDocument wikiDocument) {
        this.wikiDocument = wikiDocument;
        return this;
    }

    public boolean isForcePermURL() {
        return forcePermURL;
    }

    public WikiRedirect setForcePermURL(boolean forcePermURL) {
        this.forcePermURL = forcePermURL;
        return this;
    }

    public String getFragment() {
        return fragment;
    }

    public WikiRedirect setFragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public boolean isPropagateConversation() {
        return propagateConversation;
    }

    public WikiRedirect setPropagateConversation(boolean propagateConversation) {
        this.propagateConversation = propagateConversation;
        return this;
    }

    public void execute() {

        WikiURLRenderer urlRenderer = WikiURLRenderer.instance();
        String url = forcePermURL
                ? urlRenderer.renderPermURL(getWikiDocument())
                : urlRenderer.renderURL(getWikiDocument());

        // TODO: Fragile?
        String conversationIdParam = Manager.instance().getConversationIdParameter();
        if (propagateConversation)  url = url + "?"+conversationIdParam+"=" + org.jboss.seam.core.Conversation.instance().getId();

        if (getFragment() != null) url = url + "#" + fragment;
        
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        try {
            log.debug("redirecting to URL: " + url);
            ctx.redirect(ctx.encodeResourceURL(url));
        } catch (IOException ioe) {
            throw new RedirectException(ioe);
        }
        FacesContext.getCurrentInstance().responseComplete();
    }

    public static WikiRedirect instance() {
        return (WikiRedirect) Component.getInstance(WikiRedirect.class);
    }
}
