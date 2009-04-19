/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.listener;

import org.hibernate.Query;
import org.hibernate.StatelessSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

import java.util.Collection;

/**
 * Moves the values of all nodes on the right side of a deleted node.
 *
 * @author Christian Bauer
 */
class DeleteNestedSetOperation extends NestedSetOperation {

    private static final Log log = LogFactory.getLog(DeleteNestedSetOperation.class);

    long databaseMoveOffset;

    public DeleteNestedSetOperation(NestedSetNode node) {
        super(node);
    }

    protected void beforeExecution() {
        databaseMoveOffset = node.getNodeInfo().getNsRight() - node.getNodeInfo().getNsLeft() + 1;
        log.trace("calculated database offset: " + databaseMoveOffset);
    }

    protected void executeOnDatabase(StatelessSession ss) {

        Query updateLeft =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nodeInfo.nsLeft = n.nodeInfo.nsLeft - :offset " +
                               " where n.nodeInfo.nsThread = :thread and n.nodeInfo.nsLeft > :right");
        updateLeft.setParameter("offset", databaseMoveOffset);
        updateLeft.setParameter("thread", node.getNodeInfo().getNsThread());
        updateLeft.setParameter("right", node.getNodeInfo().getNsRight());
        int updateLeftCount = updateLeft.executeUpdate();
        log.trace("updated left values of nested set nodes: " + updateLeftCount);

        Query updateRight =
                ss.createQuery("update " + nodeEntityName + " n set " +
                               " n.nodeInfo.nsRight = n.nodeInfo.nsRight - :offset " +
                               " where n.nodeInfo.nsThread = :thread and n.nodeInfo.nsRight > :right");
        updateRight.setParameter("offset", databaseMoveOffset);
        updateRight.setParameter("thread", node.getNodeInfo().getNsThread());
        updateRight.setParameter("right", node.getNodeInfo().getNsRight());
        int updateRightCount = updateRight.executeUpdate();
        log.trace("updated right values of nested set nodes: " + updateRightCount);
    }

    protected void executeInMemory(Collection<NestedSetNode> nodesInPersistenceContext) {
        log.trace("updating in memory nodes (flat) in the persistence context: " + nodesInPersistenceContext.size());

        for (NestedSetNode n: nodesInPersistenceContext) {

            if (n.getNodeInfo().getNsThread().equals(node.getNodeInfo().getNsThread())
                && n.getNodeInfo().getNsRight() > node.getNodeInfo().getNsRight()) {

                n.getNodeInfo().setNsRight(n.getNodeInfo().getNsRight() - databaseMoveOffset);
            }

            if (n.getNodeInfo().getNsThread().equals(node.getNodeInfo().getNsThread())
                && n.getNodeInfo().getNsLeft() > node.getNodeInfo().getNsRight()) {

                n.getNodeInfo().setNsLeft(n.getNodeInfo().getNsLeft() - databaseMoveOffset);
            }
        }
    }
}
