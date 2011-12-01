package org.jboss.seam.security.examples.openid;

import javax.enterprise.inject.Model;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.security.external.dialogues.api.DialogueManager;
import org.jboss.seam.security.external.openid.api.OpenIdProviderApi;

@Model
public class Login {
    @Inject
    private OpenIdProviderApi opApi;

    private String userNameReceivedFromRp;

    private String realm;

    private String userName;

    @Inject
    private DialogueManager dialogueManager;

    @Inject
    private OpIdentity identity;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserNameReceivedFromRp() {
        return userNameReceivedFromRp;
    }

    public void setUserNameReceivedFromRp(String userNameReceivedFromRp) {
        this.userNameReceivedFromRp = userNameReceivedFromRp;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public boolean isDialogueActive() {
        return dialogueManager.isAttached();
    }

    public String login() {
        String userName = userNameReceivedFromRp != null ? userNameReceivedFromRp : this.userName;
        identity.localLogin(userName);
        if (dialogueManager.isAttached()) {
            opApi.authenticationSucceeded(userName, (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
            return null;
        } else {
            return "LOCAL_LOGIN";
        }
    }

    public void cancel() {
        if (dialogueManager.isAttached()) {
            opApi.authenticationFailed((HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse());
        } else {
            throw new IllegalStateException("cancel method can only be called during an OpenID login");
        }
    }
}
