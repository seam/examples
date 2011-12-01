package org.jboss.seam.security.examples.idmconsole.action;

import java.io.Serializable;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.security.Identity;
import org.jboss.seam.transaction.Transactional;
import org.picketlink.idm.api.Credential;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.PasswordCredential;

public
@Transactional
@Model
class ChangePasswordAction implements Serializable {
    private static final long serialVersionUID = -8727330690588109980L;

    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    @Inject
    Identity identity;
    @Inject
    IdentitySession identitySession;

    public String changePassword() throws IdentityException {
        if (!identitySession.getAttributesManager().validateCredentials(identity.getUser(),
                new Credential[]{new PasswordCredential(oldPassword)})) {
            // TODO add a message

            return "failed";
        }

        if (!confirmPassword.equals(newPassword)) {
            // TODO add a message

            return "failed";
        }

        identitySession.getAttributesManager().updatePassword(identity.getUser(), newPassword);
        return "success";
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
