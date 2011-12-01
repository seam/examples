package org.jboss.seam.security.examples.id_consumer;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.jboss.seam.security.events.DeferredAuthenticationEvent;
import org.jboss.seam.security.external.api.ResponseHolder;
import org.jboss.seam.security.external.openid.OpenIdAuthenticator;
import org.jboss.seam.security.external.openid.api.OpenIdPrincipal;
import org.jboss.seam.security.external.spi.OpenIdRelyingPartySpi;

/**
 * This bean processes the login succeeded and failed events and takes care
 * of redirecting the user to the appropriate page.
 *
 * @author Shane Bryzak
 */
public class OpenIdRelyingPartySpiImpl implements OpenIdRelyingPartySpi {
    @Inject
    private ServletContext servletContext;

    @Inject
    OpenIdAuthenticator openIdAuthenticator;

    @Inject
    Event<DeferredAuthenticationEvent> deferredAuthentication;

    public void loginSucceeded(OpenIdPrincipal principal, ResponseHolder responseHolder) {
        try {
            openIdAuthenticator.success(principal);
            deferredAuthentication.fire(new DeferredAuthenticationEvent(true));

            responseHolder.getResponse().sendRedirect(servletContext.getContextPath() + "/UserInfo.jsf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginFailed(String message, ResponseHolder responseHolder) {
        try {
            responseHolder.getResponse().sendRedirect(servletContext.getContextPath() + "/AuthenticationFailed.jsf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
