package org.jboss.seam.security.examples.idmconsole.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;

/**
 * Action class used to search for role types
 *
 * @author Shane Bryzak
 */
public
@Model
class RoleSearch implements Serializable {
    private static final long serialVersionUID = -1014495134519417515L;

    @Inject
    IdentitySession identitySession;

    private List<String> roleTypes;

    @Inject
    public void loadRoleTypes() throws IdentityException, FeatureNotSupportedException {
        roleTypes = new ArrayList<String>();

        for (RoleType roleType : identitySession.getRoleManager().findRoleTypes()) {
            roleTypes.add(roleType.getName());
        }
    }

    public List<String> getRoleTypes() {
        return roleTypes;
    }
}
