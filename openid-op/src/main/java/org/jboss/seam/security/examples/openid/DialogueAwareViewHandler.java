package org.jboss.seam.security.examples.openid;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.security.external.ResponseHolderImpl;
import org.jboss.seam.security.external.api.ResponseHolder;
import org.jboss.seam.security.external.dialogues.DialogueBeanProvider;

/**
 * @author Marcel Kolsteren
 */
public class DialogueAwareViewHandler extends ViewHandlerWrapper {
    private ViewHandler delegate;

    public DialogueAwareViewHandler(ViewHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getActionURL(FacesContext facesContext, String viewId) {
        String actionUrl = super.getActionURL(facesContext, viewId);
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        if (DialogueBeanProvider.dialogueManager(servletContext).isAttached()) {
            String dialogueId = DialogueBeanProvider.dialogue(servletContext).getId();
            ResponseHolder responseHolder = new ResponseHolderImpl((HttpServletResponse) facesContext.getExternalContext().getResponse(), dialogueId);
            return responseHolder.addDialogueIdToUrl(actionUrl);
        } else {
            return actionUrl;
        }
    }

    /**
     * @see {@link ViewHandlerWrapper#getWrapped()}
     */
    @Override
    public ViewHandler getWrapped() {
        return delegate;
    }

}
