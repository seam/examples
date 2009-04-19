/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import static org.jboss.seam.annotations.Install.APPLICATION;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.model.Role;

/**
 * Need this until Drools fixes bugs and becomes usable/debuggable.
 *
 */
@Name("org.jboss.seam.security.identity")
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Install(precedence=APPLICATION)
@AutoCreate
@Startup
public class WikiIdentity extends Identity {

    private User currentUser;
    private Integer currentAccessLevel;

    // We don't care if a user is logged in, just check it...
    public void checkRestriction(String expr) {
        if (!evaluateExpression(expr)) {
            Events.instance().raiseEvent("org.jboss.seam.notAuthorized");
            throw new AuthorizationException(String.format(
                    "Authorization check failed for expression [%s]", expr));
        }
    }

    public boolean hasPermission(String name, String action, Object... args) {

        currentUser = (User)Component.getInstance("currentUser");
        currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");

        if (args == null || args.length == 0) {
            // All the security checks currently need arguments...
            return false;
        }

        if ("Node".equals(name) && "create".equals(action)) {
            return checkCreateAccess( (WikiNode)args[0]);
        } else
        if ("Node".equals(name) && "edit".equals(action)) {
            return checkEditAccess((WikiNode)args[0]);
        } else
        if ("Node".equals(name) && "read".equals(action)) {
            return checkReadAccess((WikiNode)args[0]);
        } else
        if ("Node".equals(name) && "changeAccessLevel".equals(action)) {
            return checkRaiseAccessLevel((WikiNode)args[0]);
        } else
        if ("User".equals(name) && "edit".equals(action)) {
            return checkEditUser((User)args[0]);
        } else
        if ("User".equals(name) && "delete".equals(action)) {
            return checkDeleteUser((User)args[0]);
        } else
        if ("User".equals(name) && "editRoles".equals(action)) {
            return checkEditUserRoles((User)args[0]);
        } else
        if ("Node".equals(name) && "editMenu".equals(action)) {
            return checkEditMenu((WikiNode)args[0]);
        } else
        if ("User".equals(name) && "isAdmin".equals(action)) {
            return checkIsAdmin((User)args[0]);
        } else
        if ("Comment".equals(name) && "create".equals(action)) {
            return checkCommentCreate((WikiDocument)args[0]);
        } else
        if ("Comment".equals(name) && "delete".equals(action)) {
            return checkCommentDelete((WikiNode)args[0]);
        } else
        if ("Trash".equals(name) && "empty".equals(action)) {
            return checkTrashEmpty((WikiDirectory)args[0]);
        } else
        if ("Feed".equals(name) && "write".equals(action)) {
            return checkFeedWrite((WikiFeed)args[0]);
        }


        return false;
    }

    /*
        User either needs to have the access level of the parent directory
        or the user is the creator of the parent directory
    */
    private boolean checkCreateAccess(WikiNode directory) {
        //if (directory.getId().equals(wikiPrefs.getMemberAreaId())) return false; // Member home dir is immutable
        if (directory.getWriteAccessLevel() == Role.GUESTROLE_ACCESSLEVEL) return true;
        int dirWriteAccessLevel = directory.getWriteAccessLevel();
        User dirCreator = directory.getCreatedBy();
        if (
            currentAccessLevel >= dirWriteAccessLevel
            ||
            currentUser.getId().equals(dirCreator.getId())
           )
           return true;

        return false;
    }

    /*
        User either needs to have the access level of the edited node or has to be the creator
    */
    private boolean checkReadAccess(WikiNode node) {
        if (node.getReadAccessLevel() == Role.GUESTROLE_ACCESSLEVEL) return true;
        int nodeReadAccessLevel = node.getReadAccessLevel();
        User nodeCreator = node.getCreatedBy();

        if (currentAccessLevel >= nodeReadAccessLevel
            ||
            currentUser.getId().equals(nodeCreator.getId())
           )
           return true;

        return false;
    }

    /*
        User either needs to have the access level of the edited node or has to be the creator,
        if the node is write protected, he needs to be admin.
    */
    private boolean checkEditAccess(WikiNode node) {
        if (node.isWriteProtected() && currentAccessLevel != Role.ADMINROLE_ACCESSLEVEL) return false;
        if (node.getWriteAccessLevel() == Role.GUESTROLE_ACCESSLEVEL) return true;
        int nodeWriteAccessLevel = node.getWriteAccessLevel();
        User nodeCreator = node.getCreatedBy();

        if (currentAccessLevel >= nodeWriteAccessLevel
            ||
            currentUser.getId().equals(nodeCreator.getId())
           )
           return true;

        return false;
    }

    /*
        User can't persist or update a node and assign a higher access level than
        he has, unless he is the creator
    */
    private boolean checkRaiseAccessLevel(WikiNode node) {
        //if (node.getId() != null && node.getId().equals(wikiPrefs.getMemberAreaId())) return false; // Member home dir is immutable
        int desiredWriteAccessLevel = node.getWriteAccessLevel();
        int desiredReadAccessLevel = node.getReadAccessLevel();
        User nodeCreator = node.getCreatedBy();

        if (
            ( desiredReadAccessLevel <= currentAccessLevel
              &&
              desiredWriteAccessLevel <= currentAccessLevel )
            ||
            ( nodeCreator == null
              ||
              currentUser.getId().equals(nodeCreator.getId()) )
           )
           return true;

        return false;
    }

    /*
        Only admins can change roles of a user
    */
    private boolean checkEditUserRoles(User currentUser) {
        return currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL;
    }

    /*
        Only admins can edit users, or the user himself
    */
    private boolean checkEditUser(User user) {
        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        if (currentUser.getId().equals(user.getId())) return true;
        return false;
    }

    /*
        Only admins can delete users and some users can't be deleted
    */
    private boolean checkDeleteUser(User user) {
        // Can't delete admin and guest accounts
        User adminUser = (User)Component.getInstance("adminUser");
        User guestUser = (User)Component.getInstance("guestUser");
        if (adminUser.getId().equals(user.getId())) return false;
        if (guestUser.getId().equals(user.getId())) return false;

        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        return false;
    }

    /*
        Admins can edit all menus, owners can edit their own.
    */
    private boolean checkEditMenu(WikiNode node) {
        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        if (node.getCreatedBy().getId().equals(currentUser.getId())) return true;
        return false;
    }

    /*
        Only admins are admins
    */
    private boolean checkIsAdmin(User user) {
        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        return false;
    }

    /*
        Only admins or enabled documents allow comments, if you can read the document.
    */
    private boolean checkCommentCreate(WikiDocument doc) {
        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        if (doc.getReadAccessLevel() <= currentAccessLevel &&
            doc.isEnableComments() && doc.isEnableCommentForm()) return true;
        return false;
    }

    /*
        Only admins can delete comments.
    */
    private boolean checkCommentDelete(WikiNode node) {
        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        return false;
    }

    /*
        Only admins can empty the trash
    */
    private boolean checkTrashEmpty(WikiDirectory trashArea) {
        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        return false;
    }

    /*
        Only admins can write to feed or users who have write permission on the associated dir.
    */
    private boolean checkFeedWrite(WikiFeed feed) {
        if (currentAccessLevel == Role.ADMINROLE_ACCESSLEVEL) return true;
        return feed.getDirectory().getWriteAccessLevel() <= currentAccessLevel;
    }

}
