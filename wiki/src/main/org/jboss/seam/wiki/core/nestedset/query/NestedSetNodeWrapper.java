/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.query;

import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

import java.util.*;
import java.io.Serializable;

/**
 * Wraps a {@link org.jboss.seam.wiki.core.nestedset.NestedSetDelegate} and links it into a read-only tree of parent and children.
 * <p>
 * This wrapper is returned by the {@link NestedSetResultTransformer}. For example,
 * you query your tree with a nested set query starting from a particular node. You
 * want all children of that start node, including their children, and so on. The
 * {@link NestedSetResultTransformer} will handle your query result, which represents
 * a flat subtree, and link together the nodes in a hierarchical fashion. You will get
 * back your start node in a {@link NestedSetNodeWrapper} and you can access the
 * children and their children, and so on, through the <tt>wrappedChildren</tt> collection
 * of the wrapper. The regular <tt>children</tt> collection of the wrapped
 * {@link org.jboss.seam.wiki.core.nestedset.NestedSetDelegate} owner instances are not initialized! Use
 * the wrapper tree to display the data or to work with the whole subtree. As a bonus you also get
 * the <tt>level</tt> of each node in the (sub)tree you queried. You can access (but not
 * modify) the linked parent of each wrapped node through <tt>wrappedParent</tt>.
 * </p>
 * <p>
 * The <tt>wrappedChildren</tt> of each wrapper are by default in a {@link java.util.List}.
 * You can also access the same nodes through the <tt>getWrappedChildrenSorted()</tt> method,
 * which returns a {@link java.util.SortedSet} that is sorted with the {@link java.util.Comparator}
 * supplied at construction time. This means that in-level sorting (how the children of a particular node
 * are sorted) does not occur in the database but in memory. This should not be a performance problem,
 * as you'd usually query for quite small subtrees, most of the time to display a
 * subtree. The comparator usually sorts the collection by some property of the
 * wrapped {@link org.jboss.seam.wiki.core.nestedset.NestedSetDelegate} owner.
 * </p>
 * <p>
 * Note: Do not modify the collections or the parent reference of the wrapper, these
 * are read-only results and modifications are not reflected in the database.
 * </p>
 *
 * @author Christian Bauer
 */
public class NestedSetNodeWrapper<N extends NestedSetNode> implements Serializable {

    N wrappedNode;
    NestedSetNodeWrapper<N> wrappedParent;
    List<NestedSetNodeWrapper<N>> wrappedChildren = new ArrayList<NestedSetNodeWrapper<N>>();
    Comparator<NestedSetNodeWrapper<N>> comparator;
    Long level;
    Map<String, Object> additionalProjections = new HashMap<String, Object>();
    Map<Long, NestedSetNodeWrapper<N>> flatTree = new LinkedHashMap<Long, NestedSetNodeWrapper<N>>();
    Object payload;

    public NestedSetNodeWrapper(N wrappedNode) {
        this(
            wrappedNode,
            // Default comparator uses identifiers of wrapped nodes
            new Comparator<NestedSetNodeWrapper<N>>() {
                public int compare(NestedSetNodeWrapper<N> o1, NestedSetNodeWrapper<N> o2) {
                    return o1.getWrappedNode().getId().compareTo(o2.getWrappedNode().getId());
                }
            }
        );
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator) {
        this(wrappedNode, comparator, 0l);
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator, Long level) {
        this(wrappedNode, comparator, level, new HashMap<String,Object>());
    }

    public NestedSetNodeWrapper(N wrappedNode, Comparator<NestedSetNodeWrapper<N>> comparator, Long level, Map<String,Object> additionalProjections) {
        if (wrappedNode == null) {
            throw new IllegalArgumentException("Can't wrap null node");
        }
        this.wrappedNode = wrappedNode;
        this.comparator = comparator;
        this.level = level;
        this.additionalProjections = additionalProjections;
    }

    public N getWrappedNode() {
        return wrappedNode;
    }

    void setWrappedNode(N wrappedNode) {
        this.wrappedNode = wrappedNode;
    }

    public NestedSetNodeWrapper<N> getWrappedParent() {
        return wrappedParent;
    }

    void setWrappedParent(NestedSetNodeWrapper<N> wrappedParent) {
        this.wrappedParent = wrappedParent;
    }

    public List<NestedSetNodeWrapper<N>> getWrappedChildren() {
        return wrappedChildren;
    }

    void setWrappedChildren(List<NestedSetNodeWrapper<N>> wrappedChildren) {
        this.wrappedChildren = wrappedChildren;
    }

    void addWrappedChild(NestedSetNodeWrapper<N> wrappedChild) {
        getWrappedChildren().add(wrappedChild);
    }

    public Comparator<NestedSetNodeWrapper<N>> getComparator() {
        return comparator;
    }

    public Long getLevel() {
        return level;
    }

    public Map<String, Object> getAdditionalProjections() {
        return additionalProjections;
    }

    public SortedSet<NestedSetNodeWrapper<N>> getWrappedChildrenSorted() {
        SortedSet<NestedSetNodeWrapper<N>> wrappedChildrenSorted = new TreeSet<NestedSetNodeWrapper<N>>(comparator);
        wrappedChildrenSorted.addAll(getWrappedChildren());
        return wrappedChildrenSorted;
    }

    public Map<Long, NestedSetNodeWrapper<N>> getFlatTree() {
        return flatTree;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    // This is needed because JSF converters for selectitems need to return an equal() instance to
    // the selected item of the selectitems collection. This sucks.
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NestedSetNodeWrapper that = (NestedSetNodeWrapper) o;

        return wrappedNode.getId().equals(that.wrappedNode.getId());

    }

    public int hashCode() {
        return wrappedNode.getId().hashCode();
    }

    public String toString() {
        return "Wrapper on level " + getLevel() + " for: " + getWrappedNode();
    }

}

