/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.FacesSecurityEvents;
import org.jboss.seam.security.Identity;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.wiki.core.action.WikiRequestResolver;

/**
 * Overrides the "login failed" message and turns it into a WARN (we don't want INFO here).
 * Transports "login successful" message across conversations (in the session) for redirect-after-login.
 *
 * @author Christian Bauer
 */
@Name("org.jboss.seam.security.facesSecurityEvents")
@Install(precedence = Install.APPLICATION, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors
@Startup
public class WikiSecurityEvents extends FacesSecurityEvents {

    @Override
    public StatusMessage.Severity getLoginFailedMessageSeverity() {
        return StatusMessage.Severity.WARN;
    }

    @Override
    @Observer(Identity.EVENT_LOGIN_SUCCESSFUL)
    public void addLoginSuccessfulMessage() {

        Contexts.getSessionContext().set(
                WikiRequestResolver.SESSION_MSG, getLoginSuccessfulMessageKey()
        );

        Contexts.getSessionContext().set(
                WikiRequestResolver.SESSION_MSG_SEVERITY, getLoginSuccessfulMessageSeverity()
        );

        Contexts.getSessionContext().set(
                WikiRequestResolver.SESSION_MSG_DATA, Identity.instance().getCredentials().getUsername()
        );

    }

}
