/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.framework.EntityNotFoundException;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.exception.InvalidWikiRequestException;
import org.jboss.seam.wiki.util.WikiUtil;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeExpandedEvent;
import org.richfaces.event.NodeSelectedEvent;

import static org.jboss.seam.international.StatusMessage.Severity.WARN;
import static org.jboss.seam.international.StatusMessage.Severity.INFO;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;

/**
 * AJAX-oriented backend for browsing directories and copy/pasting files.
 * 
 * @author Christian Bauer
 */
@Name("directoryBrowser")
@Scope(ScopeType.CONVERSATION)
public class DirectoryBrowser implements Serializable {

    @Logger
    Log log;

    @In
    Clipboard clipboard;

    @In
    StatusMessages statusMessages;

    @In
    EntityManager restrictedEntityManager;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    WikiDirectory wikiRoot;

    @In(value = "directoryBrowserSettings")
    DirectoryBrowserSettings settings;

    @Create
    @Begin // This conversation ends through timeout only
    public void create() {
        log.debug("instantiating directory browser conversation");
        resetPager();
    }

    private Long directoryId;
    private WikiDirectory instance;
    private NestedSetNodeWrapper<WikiDirectory> treeRoot;
    private List<WikiNode> childNodes;
    private Map<WikiNode, Boolean> selectedNodes = new HashMap<WikiNode,Boolean>();
    private Pager pager;

    public Long getDirectoryId() { return directoryId; }
    public void setDirectoryId(Long directoryId) { this.directoryId = directoryId; }

    public List<WikiNode> getChildNodes() { return childNodes; }
    public Map<WikiNode, Boolean> getSelectedNodes() { return selectedNodes; }

    public EntityManager getEntityManager() { return restrictedEntityManager; }

    public WikiNodeDAO getWikiNodeDAO() { return wikiNodeDAO; }

    public WikiDirectory getInstance() {
        if (instance == null) findInstance();
        return instance;
    }

    public void setInstance(WikiDirectory instance) {
        this.instance = instance;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    private void resetPager() {
        pager = new Pager(settings.getPageSize());
    }

    public NestedSetNodeWrapper<WikiDirectory> getTreeRoot() {
        if (treeRoot == null) loadTree();
        return treeRoot;
    }

    public void showTree() {
        settings.setTreeVisible(true);
    }

    public void hideTree() {
        settings.setTreeVisible(false);
    }

    // Open a node in the visible UI tree if its identifier is in the current path
    public boolean adviseTreeNodeOpened(UITree tree) {

        // We need to call the undocumented getRowData() and not getTreeNode() because we
        // use the rich:recursiveTreeNodeAdapter...
        if (tree.getRowData() == null) return false; // Safety against RichFaces behavior
        Long currentTreeNodeId = ((NestedSetNodeWrapper<WikiDirectory>) tree.getRowData()).getWrappedNode().getId();

        if (settings.getExpandedTreeNodes().contains(currentTreeNodeId)) {
            log.debug("node is stored as expanded in session: " + currentTreeNodeId);
            return true;
        }

        if (getInstance().getPathIdentifiers().contains(currentTreeNodeId)) {
            log.debug("node is in parent path of current directory, hence expanded: " + currentTreeNodeId);
            return true;
        }
        log.debug("node is not expanded: " + currentTreeNodeId);
        return false;
    }

    // Select the node in the visible UI tree if its identifier is the same as the current directory
    public boolean adviseTreeNodeSelected(UITree tree) {
        if (tree.getRowData() == null) return false; // Safety against RichFaces behavior
        Long currentTreeNodeId = ((NestedSetNodeWrapper<WikiDirectory>) tree.getRowData()).getWrappedNode().getId();
        return getInstance().getId().equals(currentTreeNodeId);
    }

    public void listenTreeNodeExpand(NodeExpandedEvent event) {
        Long currentTreeNodeId =
                ((NestedSetNodeWrapper<WikiDirectory>) ((HtmlTree)event.getSource()).getRowData()).getWrappedNode().getId();

        boolean isExpanded = ((HtmlTree)event.getSource()).isExpanded();
        if (isExpanded) {
            log.debug("expanding tree node: " + currentTreeNodeId);
            settings.getExpandedTreeNodes().add(currentTreeNodeId);
        } else {
            log.debug("collapsing tree node: " + currentTreeNodeId);
            settings.getExpandedTreeNodes().remove(currentTreeNodeId);
        }
    }

    public void listenTreeNodeSelected(NodeSelectedEvent event) {
        Long currentTreeNodeId =
                ((NestedSetNodeWrapper<WikiDirectory>) ((HtmlTree)event.getSource()).getRowData()).getWrappedNode().getId();
        log.debug("selecting tree node: " + currentTreeNodeId);
        selectDirectory(currentTreeNodeId);
    }

    public void findInstance() {
        if (getDirectoryId() == null)
            throw new InvalidWikiRequestException("Missing directoryId parameter");

        instance = wikiNodeDAO.findWikiDirectory(getDirectoryId());
        if (instance == null)
            throw new EntityNotFoundException(getDirectoryId(), WikiDirectory.class);

        afterNodeFound();
    }

    public void afterNodeFound() {
        refreshChildNodes();
    }

    public void selectDirectory(Long nodeId) {
        resetPager();
        setDirectoryId(nodeId);
        instance = null;
        findInstance();
        settings.getExpandedTreeNodes().add(nodeId);
    }

    public void sortBy(String propertyName) {
        resetPager();
        settings.setOrderByProperty(WikiNode.SortableProperty.valueOf(propertyName));
        settings.setOrderDescending(!settings.isOrderDescending());
        refreshChildNodes();
    }

    public void changePageSize() {
        pager.setPage(0);
        refreshChildNodes();
    }

    @Observer(value = {"Node.removed"}, create = false)
    public void loadTree() {
        WikiDirectory wikiRoot = (WikiDirectory) Component.getInstance("wikiRoot");
        treeRoot = wikiNodeDAO.findWikiDirectoryTree(wikiRoot);
    }

    @Observer(value = {"Node.removed", "Pager.pageChanged"}, create = false)
    public void refreshChildNodes() {

        log.debug("refreshing child nodes of current directory: " + getInstance());
        getPager().setNumOfRecords(wikiNodeDAO.findChildrenCount(getInstance()));
        getPager().setPageSize(settings.getPageSize());

        log.debug("number of children: " + getPager().getNumOfRecords());
        if (getPager().getNumOfRecords() > 0) {
            log.debug("loading children page from: " + getPager().getNextRecord() + " size: " + getPager().getPageSize());
            childNodes =
                    wikiNodeDAO.findChildren(
                            getInstance(),
                            settings.getOrderByProperty(),
                            !settings.isOrderDescending(),
                            getPager().getQueryFirstResult(),
                            getPager().getQueryMaxResults()
                    );
        } else {
            childNodes = Collections.emptyList();
        }
    }

    // TODO: Most of this clipboard stuff is based on the hope that nobody modifies anything while we have it in the clipboard...

    public void clearClipboard() {
        clipboard.clear();
    }

    public void copy() {
        for (Map.Entry<WikiNode, Boolean> entry : selectedNodes.entrySet()) {
            if (entry.getValue()) { // Has to be true for a selected node
                log.debug("copying to clipboard: " + entry.getKey());
                clipboard.add(entry.getKey().getId(), false);
            }
        }
        selectedNodes.clear();
    }

    @Restrict("#{s:hasPermission('Node', 'edit', directoryBrowser.instance)}")
    public void cut() {
        for (Map.Entry<WikiNode, Boolean> entry : selectedNodes.entrySet()) {
            if (entry.getValue()) { // Has to be true for a selected node
                log.debug("cutting to clipboard: " + entry.getKey());
                clipboard.add(entry.getKey().getId(), true);
            }
        }
        selectedNodes.clear();
        refreshChildNodes();
    }

    @Restrict("#{s:hasPermission('Node', 'create', directoryBrowser.instance)}")
    public void paste() {

        if (getInstance().getId().equals(wikiRoot.getId())) return; // Can't paste in wiki root

        // Batch the work
        int batchSize = 2;
        int i = 0;
        List<Long> batchIds = new ArrayList<Long>();
        for (Long clipboardNodeId : clipboard.getItems()) {
            i++;
            batchIds.add(clipboardNodeId);
            if (i % batchSize == 0) {
                List<WikiNode> nodesForPasteBatch = wikiNodeDAO.findWikiNodes(batchIds);
                pasteNodes(nodesForPasteBatch);
                batchIds.clear();
            }
        }
        // Last batch
        if (batchIds.size() != 0) {
            List<WikiNode> nodesForPasteBatch = wikiNodeDAO.findWikiNodes(batchIds);
            pasteNodes(nodesForPasteBatch);
        }

        log.debug("completed executing paste, refreshing...");

        selectedNodes.clear();
        clipboard.clear();
        refreshChildNodes();
    }

    private void pasteNodes(List<WikiNode> nodes) {
        log.debug("executing paste batch");
        for (WikiNode n: nodes) {
            log.debug("pasting clipboard item: " + n);
            String pastedName = n.getName();

            // Check unique name if we are not cutting and pasting into the same area
            if (!(clipboard.isCut(n.getId()) && n.getParent().getAreaNumber().equals(getInstance().getAreaNumber()))) {
                log.debug("pasting node into different area, checking wikiname");

                if (!wikiNodeDAO.isUniqueWikiname(getInstance().getAreaNumber(), WikiUtil.convertToWikiName(pastedName))) {
                    log.debug("wikiname is not unique, renaming");
                    if (pastedName.length() > 245) {
                        statusMessages.addToControlFromResourceBundleOrDefault(
                            "name",
                            WARN,
                            "lacewiki.msg.Clipboard.DuplicatePasteNameFailure",
                            "The name '{0}' was already in use in this area and is too long to be renamed, skipping paste.",
                            pastedName
                        );
                        continue; // Jump to next loop iteration when we can't append a number to the name
                    }

                    // Now try to add "Copy 1", "Copy 2" etc. to the name until it is unique
                    int i = 1;
                    String attemptedName = pastedName + " " + Messages.instance().get("lacewiki.label.Clipboard.CopySuffix") + i;
                    while (!wikiNodeDAO.isUniqueWikiname(getInstance().getAreaNumber(), WikiUtil.convertToWikiName(attemptedName))) {
                        attemptedName = pastedName + " " + Messages.instance().get("lacewiki.label.Clipboard.CopySuffix") + (++i);
                    }
                    pastedName = attemptedName;

                    statusMessages.addToControlFromResourceBundleOrDefault(
                        "name",
                        INFO,
                        "lacewiki.msg.Clipboard.DuplicatePasteName",
                        "The name '{0}' was already in use in this area, renamed item to '{1}'.",
                        n.getName(), pastedName
                    );
                }
            }

            if (clipboard.isCut(n.getId())) {
                log.debug("cut pasting: " + n);

                // Check if the cut item was a default file for its parent
                if ( ((WikiDirectory)n.getParent()).getDefaultFile() != null &&
                    ((WikiDirectory)n.getParent()).getDefaultFile().getId().equals(n.getId())) {
                    log.debug("cutting default file of directory: " + n.getParent());
                    ((WikiDirectory)n.getParent()).setDefaultFile(null);
                }

                n.setName(pastedName);
                n.setWikiname(WikiUtil.convertToWikiName(pastedName));
                n.setParent(getInstance());

                // If we cut and paste into a new area, all children must be updated as well
                if (!getInstance().getAreaNumber().equals(n.getAreaNumber())) {
                    n.setAreaNumber(getInstance().getAreaNumber());

                    // TODO: Ugly and memory intensive, better use a database query but HQL updates are limited with joins
                    if (n.isInstance(WikiDocument.class)) {
                        List<WikiComment> comments = wikiNodeDAO.findWikiCommentsFlat((WikiDocument)n, true);
                        for (WikiComment comment : comments) {
                            comment.setAreaNumber(n.getAreaNumber());
                        }
                    }
                }

            } else {
                log.debug("copy pasting: " + n);
                WikiNode newNode = n.duplicate(true);
                newNode.setName(pastedName);
                newNode.setWikiname(WikiUtil.convertToWikiName(pastedName));
                newNode.setParent(getInstance());
                newNode.setAreaNumber(getInstance().getAreaNumber());
                UserDAO userDAO = (UserDAO)Component.getInstance(UserDAO.class);
                newNode.setCreatedBy(userDAO.findUser(n.getCreatedBy().getId()));
                if (n.getLastModifiedBy() != null) {
                    newNode.setLastModifiedBy(userDAO.findUser(n.getLastModifiedBy().getId()));
                }
                restrictedEntityManager.persist(newNode);
            }
        }
        log.debug("completed executing of paste batch");
    }

    @Restrict("#{s:hasPermission('Trash', 'empty', trashArea)}")
    public void emptyTrash() {
        WikiDirectory trashArea = (WikiDirectory) Component.getInstance("trashArea");
        if (getInstance() == null || !trashArea.getId().equals(getInstance().getId())) return;

        log.debug("emptying trash");
        List<WikiNode> children = wikiNodeDAO.findChildren(getInstance(), WikiNode.SortableProperty.name, false, 0, Integer.MAX_VALUE);

        // TODO: This should be batched with a database cursor!
        for (WikiNode child : children) {
            log.debug("trashing item: " + child);
            if (child.isInstance(WikiDocument.class)) {
                NodeRemover documentRemover = (NodeRemover)Component.getInstance(DocumentNodeRemover.class);
                documentRemover.removeDependencies(child);
            } else if (child.isInstance(WikiUpload.class)) {
                NodeRemover uploadRemover = (NodeRemover)Component.getInstance(UploadNodeRemover.class);
                uploadRemover.removeDependencies(child);
            }
            restrictedEntityManager.remove(child);
        }
        restrictedEntityManager.flush();

        statusMessages.addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Trash.Emptied",
                "All items in the trash have been permanently deleted."
        );

        selectedNodes.clear();
        refreshChildNodes();
    }


    // TODO: I'm not too happy with this, maybe we should call the NodeRemovers directly from the XHTML

    // Cache removablity information, speeds up large lists
    Map<Long, Boolean> childNodesRemovability = new HashMap<Long, Boolean>();

    public boolean isRemovable(WikiNode node) {

        if (childNodesRemovability.containsKey(node.getId())) {
            // Return cached result
            return childNodesRemovability.get(node.getId());
        }

        log.debug("checking removablity of node: " + node);

        // Check if the current directory is the trash area, delete doesn't make sense here
        WikiDirectory trashArea = (WikiDirectory)Component.getInstance("trashArea");
        if (trashArea.getId().equals(getInstance().getId()))
            return false;

        // Check permissions TODO: This duplicates the check
        if (!Identity.instance().hasPermission("Node", "edit", node)) {
            log.debug("user doesn't have edit permissions for this node: " + node);
            return false;
        }

        NodeRemover remover;
        if (node.isInstance(WikiDocument.class)) {
            remover = (NodeRemover) Component.getInstance(DocumentNodeRemover.class);
        } else if (node.isInstance(WikiUpload.class)) {
            remover = (NodeRemover) Component.getInstance(UploadNodeRemover.class);
        } else {
            log.warn("no remover found for node type: " + node);
            return false;
        }
        boolean removable = remover.isRemovable(node);
        log.debug("remover said it's removable: " + removable);

        childNodesRemovability.put(node.getId(), removable);

        return removable;
    }

}
