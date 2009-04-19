/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.WikiNode;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("directoryBrowserSettings")
@Scope(ScopeType.SESSION)
@AutoCreate
public class DirectoryBrowserSettings implements Serializable {

    private long pageSize = 15l;
    private boolean treeVisible = false;
    private Set<Long> expandedTreeNodes = new HashSet<Long>();
    private WikiNode.SortableProperty orderByProperty = WikiNode.SortableProperty.name;
    private boolean orderDescending;

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isTreeVisible() {
        return treeVisible;
    }

    public void setTreeVisible(boolean treeVisible) {
        this.treeVisible = treeVisible;
    }

    public Set<Long> getExpandedTreeNodes() {
        return expandedTreeNodes;
    }

    public void setExpandedTreeNodes(Set<Long> expandedTreeNodes) {
        this.expandedTreeNodes = expandedTreeNodes;
    }

    public WikiNode.SortableProperty getOrderByProperty() {
        return orderByProperty;
    }

    public void setOrderByProperty(WikiNode.SortableProperty orderByProperty) {
        this.orderByProperty = orderByProperty;
    }

    public boolean isOrderDescending() {
        return orderDescending;
    }

    public void setOrderDescending(boolean orderDescending) {
        this.orderDescending = orderDescending;
    }
}
