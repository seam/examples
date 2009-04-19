package org.jboss.seam.wiki.core.nestedset;

import org.hibernate.annotations.Parent;

import javax.persistence.Embeddable;
import javax.persistence.Column;

@Embeddable
public class EmbeddableNestedSetDelegate<N extends NestedSetDelegateOwner> implements NestedSetDelegate<N> {

    @Parent
    private N owner;

    @Column(name = "NS_THREAD", nullable = false, updatable = false)
    private Long nsThread = 0l;

    @Column(name = "NS_LEFT", nullable = false, updatable = false)
    private Long nsLeft = 0l;

    @Column(name = "NS_RIGHT", nullable = false,  updatable = false)
    private Long nsRight = 0l;

    protected EmbeddableNestedSetDelegate() {}

    public EmbeddableNestedSetDelegate(N owner) {
        this.owner = owner;
    }

    public EmbeddableNestedSetDelegate(N owner, EmbeddableNestedSetDelegate<N> copyDelegate) {
        this.owner = copyDelegate.getOwner();
        this.nsLeft = copyDelegate.nsLeft;
        this.nsRight = copyDelegate.nsRight;
        this.nsThread = copyDelegate.nsThread;
    }

    public Long getId() {
        return getOwner().getId();
    }

    public N getOwner() {
        return owner;
    }

    private void setOwner(N owner) {
        this.owner = owner;
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
