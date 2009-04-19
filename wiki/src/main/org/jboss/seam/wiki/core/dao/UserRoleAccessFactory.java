package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.wiki.core.action.prefs.UserManagementPreferences;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.preferences.Preferences;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Name("userRoleAccessFactory")
public class UserRoleAccessFactory implements Serializable {

    @In
    EntityManager entityManager;

    // Anonymous (not logged-in) user
    @Factory(value = "currentUser", scope = ScopeType.SESSION, autoCreate = true)
    public User getCurrentUser() {
        return getGuestUser();
    }

    // Anonymous (not logged-in) user's access level
    @Factory(value = "currentAccessLevel", scope = ScopeType.SESSION, autoCreate = true)
    @BypassInterceptors // Don't inject the entityManager, this factory is called when the entityManager is created!
    public Integer getCurrentAccessLevel() {
        return Role.GUESTROLE_ACCESSLEVEL;
    }

    @Factory(value = "guestUser", scope = ScopeType.SESSION)
    public User getGuestUser() {
        try {
            User guestUser =
                    (User) entityManager
                            .createQuery("select u from User u left join fetch u.roles where u.username = '"+User.GUEST_USERNAME+"'")
                            .setHint("org.hibernate.cacheable", true)
                            .getSingleResult();
            if (guestUser.getRoles().size() > 1 || guestUser.getRoles().size() == 0) {
                throw new RuntimeException("Your '"+User.GUEST_USERNAME+"' user has none or more than one role assigned, illegal database state");
            }
            if (guestUser.getRoles().iterator().next().getAccessLevel() != Role.GUESTROLE_ACCESSLEVEL) {
                throw new RuntimeException("Your '"+User.GUEST_USERNAME+"' user isn't assigned to the guest role (access level "+Role.GUESTROLE_ACCESSLEVEL+")");
            }
            return guestUser;
        } catch (NoResultException ex) {
            throw new RuntimeException("You need to INSERT a user with username '"+User.GUEST_USERNAME+"' into the database");
        }
    }


    @Factory(value = "adminUser", scope = ScopeType.SESSION)
    public User getAdminUser() {
        try {
            User adminUser =
                    (User) entityManager
                            .createQuery("select u from User u left join fetch u.roles where u.username = '"+User.ADMIN_USERNAME+"'")
                            .setHint("org.hibernate.cacheable", true)
                            .getSingleResult();
            if (adminUser.getRoles().size() > 1 || adminUser.getRoles().size() == 0) {
                throw new RuntimeException("Your '"+User.ADMIN_USERNAME+"' user has none or more than one role assigned, illegal database state");
            }
            if (adminUser.getRoles().iterator().next().getAccessLevel() != Role.ADMINROLE_ACCESSLEVEL) {
                throw new RuntimeException("Your '"+User.ADMIN_USERNAME+"' user isn't assigned to the admin role (access level "+Role.ADMINROLE_ACCESSLEVEL+")");
            }
            return adminUser;
        } catch (NoResultException ex) {
            throw new RuntimeException("You need to INSERT a user with username '"+User.ADMIN_USERNAME+"' into the database");
        }
    }

    @Factory(value = "guestRole", scope = ScopeType.SESSION)
    public Role getGuestRole() {
        try {
            return (Role) entityManager
                    .createQuery("select r from Role r where r.accessLevel = '"+Role.GUESTROLE_ACCESSLEVEL+"'")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new RuntimeException("You need to INSERT a role with accesslevel '"+Role.GUESTROLE_ACCESSLEVEL+"' (the guest role) into the database");
        }
    }

    @Factory(value = "adminRole", scope = ScopeType.SESSION)
    public Role getAdminRole() {
        try {
            return (Role) entityManager
                    .createQuery("select r from Role r where r.accessLevel = '"+Role.ADMINROLE_ACCESSLEVEL+"'")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new RuntimeException("You need to INSERT a role with accesslevel '"+Role.ADMINROLE_ACCESSLEVEL+"' (the admin role) into the database");
        }
    }

    @Factory(value = "newUserDefaultRole", scope = ScopeType.SESSION)
    public Role getDefaultRole() {
        UserManagementPreferences userPrefs = Preferences.instance().get(UserManagementPreferences.class);
        try {
            return (Role) entityManager
                    .createQuery("select r from Role r where r.name = '"+userPrefs.getNewUserInRole()+"'")
                    .setHint("org.hibernate.cacheable", true)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new RuntimeException("Configured default role for new users '"+userPrefs.getNewUserInRole()+"' not found");
        }
    }

    /**
     * A <tt>List</tt> of all roles in the system.
     */
    @Name("rolesList")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class RoleList implements Serializable {

        @In
        protected EntityManager entityManager;

        protected List<Role> roles;

        @Unwrap
        @SuppressWarnings({"unchecked"})
        public List<Role> getRoles() {
            if (roles == null) {
                roles = (List<Role>) entityManager
                        .createQuery("select r from Role r order by r.accessLevel desc, r.displayName asc")
                        .setHint("org.hibernate.cacheable", true)
                        .getResultList();
                if (roles.size() < 2)
                    throw new RuntimeException("You need to INSERT at least two roles into the database, " +
                                               "with access level '"+Role.GUESTROLE_ACCESSLEVEL+"' and '"+Role.ADMINROLE_ACCESSLEVEL+"'");
                }
            return roles;
        }

    }

    /**
     * Aggregates role names with access level integers, e.g.
     * <pre>
     * Access Level, Role Name
     *  1             Foo
     *  2             Bar
     *  2             Baz
     *  1000          Admin
     * </pre>
     * is aggregated for display into
     * <pre>
     * Access Level, Role Name
     *  1             Foo
     *  2             Bar, Baz
     *  1000          Owner, Admin
     * </pre>
     */
    @Name("accessLevelsList")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class AccessLevelsList implements Serializable {

        @In
        List<Role> rolesList;

        List<Role.AccessLevel> accessLevelsList;

        @Unwrap
        public List<Role.AccessLevel> getAccessLevelList() {
            if (accessLevelsList == null) {
                accessLevelsList = new ArrayList<Role.AccessLevel>(rolesList.size());
                for (Role role : rolesList) {

                    // Create an access level object, append fake role name "Owner" if the
                    // access level is the superuser level
                    Role.AccessLevel newAccessLevel =
                            new Role.AccessLevel(
                                    role.getAccessLevel(),
                                    role.getAccessLevel() == Role.ADMINROLE_ACCESSLEVEL
                                        ? "Owner, " + role.getDisplayName()
                                        : role.getDisplayName()
                            );

                    // Put into list, if this level already exists only append the role names
                    if (accessLevelsList.contains(newAccessLevel)) {
                        Role.AccessLevel existingAccessLevel =
                                accessLevelsList.get(accessLevelsList.indexOf(newAccessLevel));
                        existingAccessLevel.appendRoleName(newAccessLevel.getRoleNames());
                    } else {
                        accessLevelsList.add(newAccessLevel);
                    }
                }
            }

            return accessLevelsList;
        }
    }

    /**
     * AccessLevel's that can be assigned by the current user, e.g. when editing a document.
     */
    @Name("assignableAccessLevelsList")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class AssignableAccessLevelsList implements Serializable {

        @In
        List<Role.AccessLevel> accessLevelsList;

        @In
        Integer currentAccessLevel;

        List<Role.AccessLevel> assignableAccessLevelsList;

        @Unwrap
        public List<Role.AccessLevel> getAssignableAccessLevelList() {
            if (assignableAccessLevelsList == null) {
                assignableAccessLevelsList = new ArrayList<Role.AccessLevel>(accessLevelsList.size());
                for (Role.AccessLevel accessLevel : accessLevelsList) {
                    // Only make access levels assignable if the current user has at least this access level
                    if (accessLevel.getAccessLevel() <= currentAccessLevel)
                        assignableAccessLevelsList.add(accessLevel);
                }
            }

            return assignableAccessLevelsList;
        }
    }

}
