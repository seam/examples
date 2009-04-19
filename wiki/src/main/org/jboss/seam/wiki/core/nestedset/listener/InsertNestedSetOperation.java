/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.listener;

import org.hibernate.StatelessSession;
import org.hibernate.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

import java.util.Collection;

/**
 * Moves the values of all nodes on the right side of an inserted node.
 *
 * @author Christian Bauer
 */
class InsertNestedSetOperation extends NestedSetOperation {

    private static final Log log = LogFactory.getLog(InsertNestedSetOperation.class);

    long spaceNeeded = 2l;
    long parentThread;
    long newLeft;
    long newRight;

    public InsertNestedSetOperation(NestedSetNode node) {
        super(node);
    }

    protected void beforeExecution() {
        log.trace("nested set insert of entity: " + nodeEntityName);

        if (node.getParentNodeInfo() == null) {
            log.trace("node has no parent, starting new thread");
            // Root node of a tree, new thread
            parentThread = node.getId();
            newLeft = 1l;
            newRight = 2l;
        } else {
            log.trace("node has a parent, appending to existing thread");
            // Child node of a parent
            parentThread = node.getParentNodeInfo().getNsThread();
            newLeft = node.getParentNodeInfo().getNsRight();
            newRight = newLeft + spaceNeeded -1;
        }

        log.trace("calculated the thread: " + parentThread + " left: " + newLeft + " right: " + newRight);
    }

    protected void executeOnDatabase(StatelessSession ss) {
        log.trace("executing nested set insert on database");

        Query updateLeft =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nodeInfo.nsLeft = n.nodeInfo.nsLeft + :spaceNeeded " +
                               " where n.nodeInfo.nsThread = :thread and n.nodeInfo.nsLeft > :right");
        updateLeft.setParameter("spaceNeeded", spaceNeeded);
        updateLeft.setParameter("thread", parentThread);
        updateLeft.setParameter("right", newLeft);
        int updateLeftCount = updateLeft.executeUpdate();
        log.trace("updated left values of nested set nodes: " + updateLeftCount);

        Query updateRight =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nodeInfo.nsRight = n.nodeInfo.nsRight + :spaceNeeded " +
                               " where n.nodeInfo.nsThread = :thread and n.nodeInfo.nsRight >= :right");
        updateRight.setParameter("spaceNeeded", spaceNeeded);
        updateRight.setParameter("thread", parentThread);
        updateRight.setParameter("right", newLeft);
        int updateRightCount = updateRight.executeUpdate();
        log.trace("updated right values of nested set nodes: " + updateRightCount);

        log.trace("updating the newly inserted row with thread, left, and right values");
        /*
            TODO: http://opensource.atlassian.com/projects/hibernate/browse/HHH-1657
        Query updateNode =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nodeInfo.nsLeft = :left, n.nodeInfo.nsRight = :right, n.nodeInfo.nsThread = :thread " +
                               " where n.id = :id");
         */
        Query updateNode = ss.getNamedQuery("updateNestedSet."+nodeEntityName);

        updateNode.setParameter("thread", parentThread);
        updateNode.setParameter("left", newLeft);
        updateNode.setParameter("right", newRight );
        updateNode.setParameter("id", node.getId());
        updateNode.executeUpdate();
    }

    protected void executeInMemory(Collection<NestedSetNode> nodesInPersistenceContext) {
        log.trace("updating in memory nodes (flat) in the persistence context: " + nodesInPersistenceContext.size());

        for (NestedSetNode n : nodesInPersistenceContext) {
            if (n.getNodeInfo().getNsThread().equals(parentThread) && n.getNodeInfo().getNsLeft() > newLeft) {
                n.getNodeInfo().setNsLeft(n.getNodeInfo().getNsLeft() + spaceNeeded);
            }
            if (n.getNodeInfo().getNsThread().equals(parentThread) && n.getNodeInfo().getNsRight() >= newLeft) {
                n.getNodeInfo().setNsRight(n.getNodeInfo().getNsRight() + spaceNeeded);
            }
        }

    }

    protected void afterExecution() {
        // Set the values of the "read-only" properties
        node.getNodeInfo().setNsThread(parentThread);
        node.getNodeInfo().setNsLeft(newLeft);
        node.getNodeInfo().setNsRight(newRight);
    }
}
