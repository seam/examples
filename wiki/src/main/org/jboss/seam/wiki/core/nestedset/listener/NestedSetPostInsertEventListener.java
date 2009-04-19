/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.listener;

import org.hibernate.ejb.event.EJB3PostInsertEventListener;
import org.hibernate.event.PostInsertEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

/**
 * Executes the nested set tree traversal after a node was inserted.
 *
 * @author Christian Bauer
 */
public class NestedSetPostInsertEventListener extends EJB3PostInsertEventListener {

    private static final Log log = LogFactory.getLog(NestedSetPostInsertEventListener.class);

    public void onPostInsert(PostInsertEvent event) {
        super.onPostInsert(event);

        if ( NestedSetNode.class.isAssignableFrom(event.getEntity().getClass())) {
            log.debug("executing nested set insert operation, recalculating the tree");
            new InsertNestedSetOperation((NestedSetNode)event.getEntity()).execute(event.getSession());
        }
    }

}