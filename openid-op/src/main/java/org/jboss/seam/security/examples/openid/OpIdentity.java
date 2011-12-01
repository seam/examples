package org.jboss.seam.security.examples.openid;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.external.openid.api.OpenIdProviderApi;

@SessionScoped
@Named
public class OpIdentity implements Serializable {
    private static final long serialVersionUID = -7096110154986991513L;

    private String userName;

    @Inject
    private OpenIdProviderApi providerApi;

    public void localLogin(String userName) {
        this.userName = userName;
    }

    public void logout() {
        if (isLoggedIn()) {
            userName = null;
            redirectToViewId("/Index.xhtml");
        } else {
            FacesMessage facesMessage = new FacesMessage("Not logged in.");
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);
        }
    }

    public boolean isLoggedIn() {
        return userName != null;
    }

    public String getUserName() {
        return userName;
    }

    public String getOpLocalIdentifier() {
        return providerApi.getOpLocalIdentifierForUserName(userName);
    }

    public void redirectToLoginIfNotLoggedIn() {
        if (!isLoggedIn()) {
            redirectToViewId("/Login.xhtml");
        }
    }

    private void redirectToViewId(String viewId) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(facesContext, null, viewId + "?faces-redirect=true");
    }
}
