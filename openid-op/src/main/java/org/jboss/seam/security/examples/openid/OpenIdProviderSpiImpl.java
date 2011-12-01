package org.jboss.seam.security.examples.openid;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.jboss.seam.security.external.api.ResponseHolder;
import org.jboss.seam.security.external.openid.api.OpenIdProviderApi;
import org.jboss.seam.security.external.openid.api.OpenIdRequestedAttribute;
import org.jboss.seam.security.external.spi.OpenIdProviderSpi;

public class OpenIdProviderSpiImpl implements OpenIdProviderSpi {
    @Inject
    private ServletContext servletContext;

    @Inject
    private OpIdentity identity;

    @Inject
    private OpenIdProviderApi opApi;

    @Inject
    private Attributes attributes;

    public void authenticate(String realm, String userName, boolean immediate, ResponseHolder responseHolder) {
        if (identity.isLoggedIn() && userName != null && !userName.equals(identity.getUserName())) {
            opApi.authenticationFailed(responseHolder.getResponse());
        } else {
            try {
                StringBuilder url = new StringBuilder();
                url.append(servletContext.getContextPath());
                url.append("/Login.jsf");
                url.append("?realm=").append(URLEncoder.encode(realm, "UTF-8"));
                if (userName != null) {
                    url.append("&userName=").append(URLEncoder.encode(userName, "UTF-8"));
                }
                responseHolder.redirectWithDialoguePropagation(url.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void fetchParameters(List<OpenIdRequestedAttribute> requestedAttributes, ResponseHolder responseHolder) {
        attributes.setRequestedAttributes(requestedAttributes);
        responseHolder.redirectWithDialoguePropagation(servletContext.getContextPath() + "/Attributes.jsf");
    }

    public boolean userExists(String userName) {
        return true;
    }
}
