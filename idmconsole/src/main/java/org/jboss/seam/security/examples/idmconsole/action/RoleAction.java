package org.jboss.seam.security.examples.idmconsole.action;

import java.io.Serializable;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.transaction.Transactional;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

/**
 * Action component for managing role types
 *
 * @author Shane Bryzak
 */
public
@Named
@ConversationScoped
class RoleAction implements Serializable {
    private static final long serialVersionUID = -4215849488301658353L;

    private String roleType;

    @Inject
    Conversation conversation;
    @Inject
    IdentitySession identitySession;

    public void createRoleType() {
        conversation.begin();
    }

    public
    @Transactional
    String deleteRoleType(String roleType) throws IdentityException, FeatureNotSupportedException {
        identitySession.getRoleManager().removeRoleType(roleType);
        return "success";
    }

    public
    @Transactional
    String save() throws IdentityException, FeatureNotSupportedException {
        identitySession.getRoleManager().createRoleType(roleType);
        conversation.end();
        return "success";
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

}
