package org.jboss.seam.security.examples.idmconsole.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.picketlink.idm.api.IdentitySearchCriteria;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.Role;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.IdentitySearchCriteriaImpl;

/**
 * Action component for locating users
 *
 * @author Shane Bryzak
 */
public
@Model
class UserSearch implements Serializable {
    private static final long serialVersionUID = 8592034786339372510L;

    List<UserDTO> users;

    @Inject
    IdentitySession identitySession;

    @Inject
    public void loadUsers() throws IdentityException {
        users = new ArrayList<UserDTO>();

        IdentitySearchCriteria criteria = new IdentitySearchCriteriaImpl();

        Collection<User> results = identitySession.getPersistenceManager().findUser(criteria);
        for (User user : results) {
            UserDTO dto = new UserDTO();
            dto.setUsername(user.getId());
            //dto.setEnabled(identityManager.isUserEnabled(user.getId()));
            users.add(dto);
        }
    }

    public String getUserRoles(String username) {
        Collection<Role> roles = null; //identityManager.getUserRoles(username);

        StringBuilder sb = new StringBuilder();

        for (Role role : roles) {
            sb.append((sb.length() > 0 ? ", " : "") + role.getRoleType().getName() +
                    ":" + role.getGroup().getName());
        }

        return sb.toString();
    }

    public List<UserDTO> getUsers() throws IdentityException {
        return users;
    }

}
