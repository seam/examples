/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.listener;

import org.hibernate.ejb.event.EJB3PostDeleteEventListener;
import org.hibernate.event.PostDeleteEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

/**
 * Executes the nested set tree traversal after a node was deleted.
 *
 * @author Christian Bauer
 */
public class NestedSetPostDeleteEventListener extends EJB3PostDeleteEventListener {

    private static final Log log = LogFactory.getLog(NestedSetPostDeleteEventListener.class);

    public void onPostDelete(PostDeleteEvent event) {
        super.onPostDelete(event);

        if ( NestedSetNode.class.isAssignableFrom(event.getEntity().getClass())) {
            log.debug("executing nested set delete operation, recalculating the tree");
            new DeleteNestedSetOperation( (NestedSetNode)event.getEntity() ).execute(event.getSession());
        }
    }

}
