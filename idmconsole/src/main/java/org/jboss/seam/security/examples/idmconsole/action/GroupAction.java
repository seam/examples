package org.jboss.seam.security.examples.idmconsole.action;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.GroupImpl;
import org.jboss.seam.transaction.Transactional;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.common.exception.IdentityException;

/**
 * Action bean for managing groups
 *
 * @author Shane Bryzak
 */
public
@Named
@ConversationScoped
class GroupAction implements Serializable {
    private static final long serialVersionUID = -1553124158319503903L;

    @Inject
    Conversation conversation;

    @Inject
    IdentitySession identitySession;

    private String groupName;

    private String groupType = "GROUP";

    public void createGroup() {
        conversation.begin();
    }

    public
    @Transactional
    void deleteGroup(String name, String groupType) throws IdentityException {
        Group group = new GroupImpl(name, groupType);
        identitySession.getPersistenceManager().removeGroup(group, true);
    }

    public
    @Transactional
    String save() throws IdentityException {
        identitySession.getPersistenceManager().createGroup(groupName, groupType);
        conversation.end();
        return "success";
    }

    public void cancel() {
        conversation.end();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public List<String> getSupportedGroupTypes() {
        return null;
    }
}
