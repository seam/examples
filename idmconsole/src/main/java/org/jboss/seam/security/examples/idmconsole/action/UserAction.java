package org.jboss.seam.security.examples.idmconsole.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.transaction.Transactional;
import org.picketlink.idm.api.Attribute;
import org.picketlink.idm.api.Group;
import org.picketlink.idm.api.IdentitySession;
import org.picketlink.idm.api.Role;
import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.api.User;
import org.picketlink.idm.common.exception.FeatureNotSupportedException;
import org.picketlink.idm.common.exception.IdentityException;
import org.picketlink.idm.impl.api.SimpleAttribute;
import org.picketlink.idm.impl.api.model.SimpleRole;
import org.picketlink.idm.impl.api.model.SimpleUser;

/**
 * A conversation-scoped component for creating and managing user accounts
 *
 * @author Shane Bryzak
 */
public
@Named
@ConversationScoped
class UserAction implements Serializable {
    private static final long serialVersionUID = 5820385095080724087L;

    private static final String ATTRIBUTE_NAME_USER_ENABLED = "USER_ENABLED";

    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private String confirm;
    private Collection<Role> roles;
    private boolean enabled;

    private boolean newUserFlag;

    @Inject
    IdentitySession identitySession;
    @Inject
    Conversation conversation;

    Collection<RoleType> roleTypes;
    Collection<Group> roleGroups;

    private RoleType roleType;
    private Group roleGroup;

    public void createUser() {
        conversation.begin();
        roles = new ArrayList<Role>();
        newUserFlag = true;
    }

    public void editUser(String username) throws IdentityException, FeatureNotSupportedException {
        conversation.begin();
        this.username = username;

        roles = new ArrayList<Role>();

        Collection<RoleType> roleTypes = identitySession.getRoleManager().findUserRoleTypes(new SimpleUser(username));

        for (RoleType roleType : roleTypes) {
            roles.addAll(identitySession.getRoleManager().findRoles(username, roleType.getName()));
        }

        Attribute enabledAttr = identitySession.getAttributesManager().getAttribute(username,
                ATTRIBUTE_NAME_USER_ENABLED);

        if (enabledAttr != null) {
            Object value = enabledAttr.getValue();
            if (value != null) {
                if (Boolean.class.isAssignableFrom(value.getClass())) {
                    enabled = (Boolean) value;
                } else {
                    enabled = Boolean.valueOf((String) value);
                }
            } else {
                enabled = true;
            }
        } else {
            enabled = true;
        }


        newUserFlag = false;
    }

    public void addRole() throws IdentityException, FeatureNotSupportedException {
        roleTypes = identitySession.getRoleManager().findRoleTypes();
        roleGroups = identitySession.getPersistenceManager().findGroup("GROUP");

        roleType = null;
        roleGroup = null;
    }

    public String roleSave() {
        roles.add(new SimpleRole(roleType, null, roleGroup));
        return "success";
    }

    public
    @Transactional
    void deleteUser(String username) throws IdentityException {
        identitySession.getPersistenceManager().removeUser(new SimpleUser(username), true);
    }

    public
    @Transactional
    String save() throws IdentityException, FeatureNotSupportedException {
        if (newUserFlag) {
            return saveNewUser();
        } else {
            return saveExistingUser();
        }
    }

    public void cancel() {
        conversation.end();
    }

    private String saveNewUser() throws IdentityException, FeatureNotSupportedException {
        if (password == null || !password.equals(confirm)) {
            // TODO - add control message
            //StatusMessages.instance().addToControl("password", "Passwords do not match");
            return "failure";
        }

        User user = identitySession.getPersistenceManager().createUser(username);
        identitySession.getAttributesManager().updatePassword(user, password);

        for (Role role : roles) {
            identitySession.getRoleManager().createRole(role.getRoleType(), user, role.getGroup());
        }

        conversation.end();

        return "success";
    }

    private String saveExistingUser() throws IdentityException, FeatureNotSupportedException {
        User user = identitySession.getPersistenceManager().findUser(username);

        // Check if a new password has been entered
        if (password != null && !"".equals(password)) {
            if (!password.equals(confirm)) {
                // TODO - add control message
                // StatusMessages.instance().addToControl("password", "Passwords do not match");
                return "failure";
            } else {
                identitySession.getAttributesManager().updatePassword(user, password);
            }
        }

        Collection<Role> grantedRoles = new ArrayList<Role>();

        Collection<RoleType> roleTypes = identitySession.getRoleManager().findUserRoleTypes(user);

        for (RoleType roleType : roleTypes) {
            grantedRoles.addAll(identitySession.getRoleManager().findRoles(username, roleType.getName()));
        }

        if (grantedRoles != null) {
            for (Role role : grantedRoles) {
                if (!roles.contains(role)) {
                    identitySession.getRoleManager().removeRole(role);
                }
            }
        }

        for (Role role : roles) {
            if (grantedRoles == null || !grantedRoles.contains(role)) {
                identitySession.getRoleManager().createRole(role.getRoleType(), user, role.getGroup());
            }
        }

        identitySession.getAttributesManager().updateAttributes(user,
                new Attribute[]{new SimpleAttribute(ATTRIBUTE_NAME_USER_ENABLED, enabled)});

        conversation.end();
        return "success";
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public Group getRoleGroup() {
        return roleGroup;
    }

    public void setRoleGroup(Group roleGroup) {
        this.roleGroup = roleGroup;
    }

    public Collection<RoleType> getRoleTypes() {
        return roleTypes;
    }

    public Collection<Group> getRoleGroups() {
        return roleGroups;
    }
}
