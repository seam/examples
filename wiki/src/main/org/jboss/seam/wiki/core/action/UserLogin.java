package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.ui.WikiURLRenderer;
import org.jboss.seam.wiki.WikiInit;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.web.ServletContexts;

/**
 * Decides what to do (redirect) after a user logs in successfully.
 * <p>
 * This is a stateful component and therefore different than <tt>WikiSecurityEvents</tt>.
 * </p>
 *
 * @author Christian Bauer
 */
@Name("userLogin")
public class UserLogin {

    protected final static String REGULAR_SESSION_MAX_INACTIVE_SECONDS = "regularSessionMaxInactiveInterval";

    @Logger
    Log log;

    @In(create = false, required = false)
    DocumentHome documentHome;

    @In
    WikiURLRenderer wikiURLRenderer;

    String redirectURL;

    @Observer(Identity.EVENT_LOGIN_SUCCESSFUL)
    public void onLogin() {

        // Store the regular session timeout value, so we can set it back later on logout
        int regularSessionTimeout = ServletContexts.getInstance().getRequest().getSession().getMaxInactiveInterval();
        Contexts.getSessionContext().set(REGULAR_SESSION_MAX_INACTIVE_SECONDS, regularSessionTimeout);
        WikiInit init = (WikiInit)Component.getInstance(WikiInit.class);
        if (init.getAuthenticatedSessionTimeoutMinutes() != 0) {
            log.debug("setting timeout of authenticated user session to minutes: " + init.getAuthenticatedSessionTimeoutMinutes());
            ServletContexts.getInstance().getRequest().getSession().setMaxInactiveInterval(
                init.getAuthenticatedSessionTimeoutMinutes()*60
            );
        }

        // Prepare redirect stuff
        if (documentHome != null && documentHome.isManaged()) {
            redirectURL = wikiURLRenderer.renderURL(documentHome.getInstance());
        } else {
            redirectURL = wikiURLRenderer.renderURL((WikiDocument)Component.getInstance("wikiStart"));
        }

        log.debug("preparing URL for redirect after successful login: " + redirectURL);

        log.debug("destroying all conversations after successful login");
        Manager.instance().killAllOtherConversations();

        // Finally, end the current one after we are done with documentHome
        Conversation.instance().endBeforeRedirect();
    }

    @Observer(Identity.EVENT_LOGGED_OUT)
    public void onLogout() {
        Object o = Contexts.getSessionContext().get(REGULAR_SESSION_MAX_INACTIVE_SECONDS);
        // Don't rely on that, do a null check - this should never be null but sometimes it is... *sigh*
        if (o != null) {
            int regularSessionTimeout = (Integer) o;
            log.debug("resetting timeout of user session after logout to minutes: " + regularSessionTimeout/60);
            ServletContexts.getInstance().getRequest().getSession().setMaxInactiveInterval(regularSessionTimeout);
        } else {
            // Safety, reset to a low value, 10 minutes
            // TODO: That value is actually configured in web.xml, how do we get it here?
            ServletContexts.getInstance().getRequest().getSession().setMaxInactiveInterval(600);
        }
    }

    public String getLoginRedirectURL() {
        log.debug("obtaining redirect URL: " + redirectURL);
        return redirectURL;
    }
}
