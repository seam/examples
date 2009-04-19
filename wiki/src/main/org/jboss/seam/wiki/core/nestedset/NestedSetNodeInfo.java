package org.jboss.seam.wiki.core.nestedset;

import org.hibernate.annotations.Parent;

import javax.persistence.Embeddable;
import javax.persistence.Column;
import java.io.Serializable;

@Embeddable
public class NestedSetNodeInfo<N extends NestedSetNode> implements Serializable {

    @Parent
    private N owner;

    @Column(name = "NS_THREAD", nullable = false, updatable = false)
    private Long nsThread = 0l;

    @Column(name = "NS_LEFT", nullable = false, updatable = false)
    private Long nsLeft = 0l;

    @Column(name = "NS_RIGHT", nullable = false,  updatable = false)
    private Long nsRight = 0l;

    protected NestedSetNodeInfo() {}

    public NestedSetNodeInfo(N owner) {
        this.owner = owner;
    }

    public NestedSetNodeInfo(NestedSetNodeInfo<N> original) {
        this.owner = original.owner;
        this.nsLeft = original.nsLeft;
        this.nsRight = original.nsRight;
        this.nsThread = original.nsThread;
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

    public String toString() {
        return "NSInfo LEFT: " + getNsLeft() + " RIGHT: " + getNsRight() + " THREAD: " + getNsThread();
    }
}
