/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.exception.InvalidWikiRequestException;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.international.StatusMessages;

import static org.jboss.seam.international.StatusMessage.Severity.WARN;
import static org.jboss.seam.international.StatusMessage.Severity.INFO;

import java.util.Date;
import java.util.List;

/**
 * Superclass for all creating and editing documents, directories, files, etc.
 *
 * @author Christian Bauer
 */
public abstract class NodeHome<N extends WikiNode, P extends WikiNode> extends EntityHome<N> {

    // TODO: This is a performance optimization, our EM is always already joined (SMPC)
    protected void joinTransaction() {}

    /* -------------------------- Context Wiring ------------------------------ */

    @In
    private WikiNodeDAO wikiNodeDAO;
    @In
    private UserDAO userDAO;
    @In
    private WikiDirectory wikiRoot;
    @In
    protected User currentUser;
    @In
    protected List<Role.AccessLevel> accessLevelsList;

    public WikiNodeDAO getWikiNodeDAO() { return wikiNodeDAO; }
    public UserDAO getUserDAO() { return userDAO; }
    public WikiDirectory getWikiRoot() { return wikiRoot; }
    public User getCurrentUser() { return currentUser; }
    public List<Role.AccessLevel> getAccessLevelsList() { return accessLevelsList; }

    /* -------------------------- Request Wiring ------------------------------ */

    private Long parentNodeId;

    public Long getParentNodeId() {
        return parentNodeId;
    }
    public void setParentNodeId(Long parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    private P parentNode;
    public P getParentNode() {
        return parentNode;
    }
    public void setParentNode(P parentNode) {
        this.parentNode = parentNode;
    }

    public void setNodeId(Long o) {
        super.setId(o);
    }
    public Long getNodeId() {
        return (Long)super.getId();
    }

    /* -------------------------- Additional States ------------------------------ */

    private boolean editor = false;

    public boolean isEditor() { return editor; }

    public void initEditor(boolean visibleWorkspace) {
        getLog().debug("initializing editor workspace");
        this.editor = true;

        if (visibleWorkspace) {
            // Set workspace description of the current conversation
            String desc = getEditorWorkspaceDescription(getNodeId() == null);
            WikiPreferences prefs = Preferences.instance().get(WikiPreferences.class);
            if (desc != null && desc.length() > prefs.getWorkspaceSwitcherDescriptionLength()) {
                desc = desc.substring(0, prefs.getWorkspaceSwitcherDescriptionLength().intValue()) + "...";
            }
            Conversation.instance().setDescription(desc);
        }
    }

    public void initEditor() {
        initEditor(true);
    }

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected String getPersistenceContextName() {
        return "restrictedEntityManager";
    }

    @Override
    public N find() {
        getLog().debug("finding an existing instance with id: " + getId());
        N foundNode = findInstance();
        if (foundNode == null) {
            handleNotFound();
            return null;
        }
        getLog().debug("found instance: " + foundNode);
        return isEditor() ? beforeNodeEditFound(afterNodeFound(foundNode)) : afterNodeFound(foundNode);
    }

    @Override
    protected N createInstance() {
        getLog().debug("creating a new instance");
        N newNode = super.createInstance();
        getLog().debug("created new instance: " + newNode);
        return isEditor() ? beforeNodeEditNew(afterNodeCreated(newNode)) : afterNodeCreated(newNode);
    }

    /* -------------------------- Basic Subclass Callbacks ------------------------------ */

    public N afterNodeCreated(N node) {

        if (parentNodeId == null)
            throw new InvalidWikiRequestException("Missing parentNodeId parameter");

        outjectCurrentLocation(node);

        return node;
    }

    public N beforeNodeEditNew(N node) {

        getLog().debug("loading parent node with id: " + parentNodeId);
        parentNode = findParentNode(parentNodeId);
        if (parentNode == null)
            throw new InvalidWikiRequestException("Could not find parent node with id: " + parentNodeId);
        getLog().debug("initalized with parent node: " + parentNode);

        // Check write access level of the parent node, if the user wants to create a new node
        if (!isPersistAllowed(node, parentNode))
            throw new AuthorizationException("You don't have permission for this operation");

        // Default to same access permissions as parent node
        node.setWriteAccessLevel(parentNode.getWriteAccessLevel());
        node.setReadAccessLevel(parentNode.getReadAccessLevel());
        writeAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(parentNode.getWriteAccessLevel())
            )
        );
        readAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(parentNode.getReadAccessLevel())
            )
        );

        return node;
    }

    public N afterNodeFound(N node) {

        getLog().debug("using parent of instance: " + node.getParent());
        if (node.getParent() != null) {  // Wiki Root doesn't have a parent
            parentNode = (P)node.getParent();
            parentNodeId = parentNode.getId();
        }

        outjectCurrentLocation(node);

        return node;
    }

    public N beforeNodeEditFound(N node) {

        // Check write access level of the node the user wants to edit
        if (!isUpdateAllowed(node, null))
            throw new AuthorizationException("You don't have permission for this operation");

        writeAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(node.getWriteAccessLevel())
            )
        );
        readAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(node.getReadAccessLevel())
            )
        );

        return node;
    }


    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {
        checkPersistPermissions();

        if (!validateComponents(getPersistValidations())) return null;

        if (!preparePersist()) return null;

        getLog().trace("linking new node with its parent node: " + getParentNode());
        getInstance().setParent(getParentNode());

        // Creation metadata
        setCreatedMetadata();

        // Wiki name conversion
        setWikiName();

        // Set its area number (if subclass didn't already set it)
        if (getInstance().getAreaNumber() == null)
            getInstance().setAreaNumber(getInstance().getParent().getAreaNumber());

        // Validate
        if (!isValidModel()) return null;

        if (!beforePersist()) return null;

        getLog().debug("persisting node: " + getInstance());
        String outcome = super.persist();
        if (outcome != null) {
            Events.instance().raiseEvent("PreferenceEditor.flushAll");
            Events.instance().raiseEvent("Node.persisted", getInstance());
        }

        // Now set the message identifier, if nobody else did
        if (getInstance().getMessageId() == null && requiresMessageId()) {
            getInstance().setMessageId(
                // Use the identifier and the creation time, both quite unique and immutable
                WikiUtil.calculateMessageId(
                    getInstance().getId(),
                    String.valueOf(getInstance().getCreatedOn().getTime()))
            );
            // Need to flush again, to execute UPDATE
            getEntityManager().flush();
        }

        return outcome;
    }

    @Override
    public String update() {
        checkUpdatePermissions();

        if (!validateComponents(getUpdateValidations())) return null;

        if (!prepareUpdate()) return null;

        // Modification metadata
        setLastModifiedMetadata();

        // Wiki name conversion
        setWikiName();

        // Validate
        if (!isValidModel()) return null;

        if (!beforeUpdate()) return null;

        getLog().debug("updating node: " + getInstance());
        String outcome = super.update();
        if (outcome != null) {
            Events.instance().raiseEvent("PreferenceEditor.flushAll");
            Events.instance().raiseEvent("Node.updated", getInstance());
        }
        return outcome;
    }

    public boolean isRemovable() {
        getLog().debug("checking removability of current instance");
        return isManaged() &&
                getNodeRemover() != null &&
                getNodeRemover().isRemovable(getInstance());
    }

    @Override
    public String remove() {
        if (!isRemovable()) return null;
        
        checkRemovePermissions();

        getLog().debug("removing node: " + getInstance());
        getNodeRemover().removeDependencies(getInstance());
        String outcome = super.remove();
        if (outcome != null) {
            Events.instance().raiseEvent("Node.removed", getInstance());
        }
        return outcome;
    }

    public String remove(Long nodeId) {
        getLog().debug("requested node remove with id: " + nodeId);
        setNodeId(nodeId);
        initEditor(false);
        String outcome = remove();
        if (outcome != null) {
            Events.instance().raiseEvent("Node.removed", getInstance());
        }
        return outcome;
    }

    public String trash() {
        if (!isRemovable()) return null;

        checkRemovePermissions();

        getLog().debug("trashing node : " + getInstance());
        getNodeRemover().trash(getInstance());
        setLastModifiedMetadata();
        getEntityManager().flush();
        trashedMessage();

        Events.instance().raiseEvent("Node.removed", getInstance());
        return "removed";
    }

    /* -------------------------- Internal (Subclass) Methods ------------------------------ */

    public abstract Class<N> getEntityClass();

    protected abstract N findInstance();

    protected abstract P findParentNode(Long parentNodeId);

    protected void outjectCurrentLocation(WikiNode node) {
        if (isPageRootController()) {
            // Outjects current node or parent directory, e.g. for breadcrumb rendering
            Contexts.getPageContext().set("currentLocation", node);
        }
    }

    protected void setWikiName() {
        getLog().trace("setting wiki name of node");
        getInstance().setWikiname(WikiUtil.convertToWikiName(getInstance().getName()));
    }

    protected void setCreatedMetadata() {
        getLog().trace("setting created metadata");
        getInstance().setCreatedBy(getCurrentUser());
        getInstance().setCreatedOn(new Date());
    }

    protected void setLastModifiedMetadata() {
        getLog().trace("setting last modified metadata");
        getInstance().setLastModifiedBy(getCurrentUser());
        getInstance().setLastModifiedOn(new Date());
    }

    protected boolean isValidModel() {
        getLog().trace("validating model");
        if (getParentNode() == null) return true; // Special case, editing the wiki root

        // Unique wiki name
        if (getWikiNodeDAO().isUniqueWikiname(getParentNode().getAreaNumber(), getInstance())) {
            return true;
        } else {
            StatusMessages.instance().addToControlFromResourceBundleOrDefault(
                "name",
                WARN,
                "lacewiki.entity.DuplicateName",
                "This name is already used, please change it"
            );
            return false;
        }

    }

    protected void checkPersistPermissions() {
        getLog().debug("checking persist permissions");
        if (!isPersistAllowed(getInstance(), getParentNode()))
            throw new AuthorizationException("You don't have permission for this operation");
    }

    protected void checkUpdatePermissions() {
        getLog().debug("checking update permissions");
        if (!isUpdateAllowed(getInstance(), getParentNode()))
            throw new AuthorizationException("You don't have permission for this operation");
    }

    protected void checkRemovePermissions() {
        getLog().debug("checking remove permissions");
        if (!isRemoveAllowed(getInstance(), getParentNode()))
            throw new AuthorizationException("You don't have permission for this operation");
    }

    protected void trashedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Node.Trashed",
                "'{0}' has been moved into the trash.",
                getInstance().getName()
        );
    }

    public boolean isPersistAllowed(N node, P parent) {
        return Identity.instance().hasPermission("Node", "create", parent);
    }

    public boolean isUpdateAllowed(N node, P parent) {
        return Identity.instance().hasPermission("Node", "edit", node);
    }

    public boolean isRemoveAllowed(N node, P parent) {
        return Identity.instance().hasPermission("Node", "edit", node);
    }

    protected boolean validateComponents(Validatable validatableComponents[]) {
        if (validatableComponents == null) return true;
        boolean allValid = true;
        for (Validatable validatableComponent : validatableComponents) {
            validatableComponent.validate();
            allValid = validatableComponent.isValid();
        }
        return allValid;
    }

    protected Validatable[] getUpdateValidations() {
        return null;
    }

    protected Validatable[] getPersistValidations() {
        return null;
    }

    protected boolean requiresMessageId() {
        return true;
    }

    /* -------------------------- Optional Subclass Callbacks ------------------------------ */

    protected boolean isPageRootController() { return true; }

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue processing
     */
    protected boolean preparePersist() { return true; }

    /**
     * Called after superclass did its preparation right before the actual persist()
     * @return boolean continue processing
     */
    protected boolean beforePersist() { return true; }

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue processing
     */
    protected boolean prepareUpdate() { return true; }

    /**
     * Called after superclass did its preparation right before the actual update()
     * @return boolean continue processing
     */
    protected boolean beforeUpdate() { return true; }

    /**
     * Called when a node is removed, obtains remover for execution of dependency deletion
     * before the node is finally removed.
     * @return NodeRemover instance
     */
    protected abstract NodeRemover getNodeRemover();

    /**
     * Description (i18n) of workspace switcher item.
     *
     * @param create true if editor is initialized to create an item, false if it's used to update an item.
     * @return String description of workspace switcher item or <tt>null</tt> if no workspace switcher item should be shown.
     */
    protected abstract String getEditorWorkspaceDescription(boolean create);

    /* -------------------------- Public Features ------------------------------ */

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void selectOwner(Long creatorId) {
        User newCreator = userDAO.findUser(creatorId);
        getInstance().setCreatedBy(newCreator);
    }

    private Role.AccessLevel writeAccessLevel;
    private Role.AccessLevel readAccessLevel;

    public Role.AccessLevel getWriteAccessLevel() {
        return writeAccessLevel;
    }

    public void setWriteAccessLevel(Role.AccessLevel writeAccessLevel) {
        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }
        this.writeAccessLevel = writeAccessLevel;
        getInstance().setWriteAccessLevel(
            writeAccessLevel != null ? writeAccessLevel.getAccessLevel() : Role.ADMINROLE_ACCESSLEVEL
        );
    }

    public Role.AccessLevel getReadAccessLevel() {
        return readAccessLevel;
    }

    public void setReadAccessLevel(Role.AccessLevel readAccessLevel) {
        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }
        this.readAccessLevel = readAccessLevel;
        getInstance().setReadAccessLevel(
            readAccessLevel != null ? readAccessLevel.getAccessLevel() : Role.ADMINROLE_ACCESSLEVEL
        );
    }

}
