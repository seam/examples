/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

/**
 * Interface implemented by domain model classes that represent a node in a nested set.
 * Usually used as a delegate, that is, an existing class that already has a parent/children
 * adjacency list adds a <tt>@OneToOne</tt> association to a delegate that implements this
 * interface. This (more or less) transparently adds nested set information to each instance.
 *
 * @author Christian Bauer
 */
public interface NestedSetDelegate<N extends NestedSetDelegateOwner> {

    /**
     * Same as <tt>getOwner().getId()</tt> but it makes our life easier.
     *
     * @return Identifier of the nested set node, i.e. the owner of the delegate.
     */
    public Long getId();

    /**
     * The domain model class that wishes to add nested set information should implement a
     * delegate to this interface. The domain model class also has to implement
     * <tt>NestedSetDelegateOwner</tt>. This method returns that owner.
     *
     * @return the owner of the <tt>NestedSetNodeDelegate</tt>, which implements <tt>NestedSetDelegateOwner</tt>
     */
    public N getOwner();

    /**
     * The root of a tree is a nested set node without a parent. Its identifier is also the thread
     * number of all nodes in that particular tree. So all children nodes (and their children, recursively)
     * need to have the same thread number. This should be mapped as a persistent property of the
     * implementor of this interface, not nullable and not updatable. Any updates that are required are
     * done transparently with event listeners.
     *
     * @return the non-nullable, persistent, and not updatable mapped persistent identifier for a particular tree
     */
    public Long getNsThread();
    public void setNsThread(Long nsThread);

    /**
     * In the nested set model, each node requires two additional attributes right visit and left visit. The tree is
     * then traversed in a modified pre-order: starting with the root node, each node is visited twice. Whenever
     * a node is entered or exited during the traversal, the sequence number of all visits is saved in
     * the current node's right visit and left visit. This is the job of the event listeners, not yours. You can
     * retrieve the current value with this method.
     *
     * @return the left value of a node
     */
    public Long getNsLeft();
    public void setNsLeft(Long nsLeft);

    /**
     * In the nested set model, each node requires two additional attributes right visit and left visit. The tree is
     * then traversed in a modified pre-order: starting with the root node, each node is visited twice. Whenever
     * a node is entered or exited during the traversal, the sequence number of all visits is saved in
     * the current node's right visit and left visit. This is the job of the event listeners, not yours. You can
     * retrieve the current value with this method.
     *
     * @return the left value of a node
     */
    public Long getNsRight();
    public void setNsRight(Long nsRight);

}
