package org.jboss.seam.security.examples.idmconsole.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.query.QueryException;
import org.picketlink.idm.common.exception.IdentityException;

/**
 * Identity management action bean for searching for groups
 *
 * @author Shane Bryzak
 */
public
@Model
class GroupSearch implements Serializable {
    private static final long serialVersionUID = 8592034786339372510L;

    List<GroupDTO> groups;

    @Inject
    IdentitySession identitySession;

    @Inject
    public void loadUsers() throws IdentityException, QueryException {
        groups = new ArrayList<GroupDTO>();

        Collection<Group> results = identitySession.getPersistenceManager().findGroup("GROUP");

        for (Group group : results) {
            GroupDTO dto = new GroupDTO();
            dto.setName(group.getName());
            dto.setGroupType(group.getGroupType());
            groups.add(dto);
        }
    }

    public List<GroupDTO> getGroups() {
        return groups;
    }
}
