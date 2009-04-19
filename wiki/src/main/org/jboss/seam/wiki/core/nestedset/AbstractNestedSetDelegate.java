/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

import javax.persistence.*;

/**
 * Utility class that implements the basic {@link NestedSetDelegate} interface.
 * <p>
 * Use this class if you already have an adjacency list model (parent/children
 * relationship mapped with a regular many-to-one property and a one-to-many collection) based
 * on a foreign key. You only need to add this superclass to a <tt>@OneToOne</tt> persistent delegate
 * entity class and you will be able to execute nested set queries on your trees and have the event listeners
 * update the nested set values of the tree (thread, left, right, of each node) if you add or
 * remove nodes.
 * 
 * @author Christian Bauer
 */
@MappedSuperclass
public abstract class AbstractNestedSetDelegate<N extends NestedSetDelegateOwner> implements NestedSetDelegate<N> {

    @Id
    @GeneratedValue(generator = "nestedSetOwnerGenerator")
    @org.hibernate.annotations.GenericGenerator(
        name = "nestedSetOwnerGenerator",
        strategy = "foreign",
        parameters = @org.hibernate.annotations.Parameter(name = "property", value = "owner")
    )
    @Column(name = "NESTED_SET_OWNER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "NESTED_SET_OWNER_ID", updatable = false, insertable = false, unique = true)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private N owner;

    @Column(name = "NS_THREAD", nullable = false, updatable = false)
    private Long nsThread = 0l;

    @Column(name = "NS_LEFT", nullable = false, updatable = false)
    private Long nsLeft = 0l;

    @Column(name = "NS_RIGHT", nullable = false,  updatable = false)
    private Long nsRight = 0l;

    protected AbstractNestedSetDelegate() {}

    public AbstractNestedSetDelegate(N owner) {
        this.owner = owner;
    }

    public AbstractNestedSetDelegate(N owner, AbstractNestedSetDelegate<N> copyDelegate) {
        this.owner = copyDelegate.getOwner();
        this.nsLeft = copyDelegate.nsLeft;
        this.nsRight = copyDelegate.nsRight;
        this.nsThread = copyDelegate.nsThread;
    }

    public Long getId() {
        return id;
    }

    public N getOwner() {
        return owner;
    }

    public Long getNsThread() {
        return nsThread;
    }

    public void setNsThread(Long nsThread) {
        this.nsThread = nsThread;
    }

    public Long getNsLeft() {
        return nsLeft;
    }

    public void setNsLeft(Long nsLeft) {
        this.nsLeft = nsLeft;
    }

    public Long getNsRight() {
        return nsRight;
    }

    public void setNsRight(Long nsRight) {
        this.nsRight = nsRight;
    }

}
