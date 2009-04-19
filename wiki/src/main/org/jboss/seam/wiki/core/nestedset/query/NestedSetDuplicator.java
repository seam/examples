/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.query;

import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

/**
 * Generate a copy of a {@link org.jboss.seam.wiki.core.nestedset.NestedSetNode}.
 *
 * <p>
 * Used by the {@link NestedSetResultTransformer} to duplicate nodes after retrieving them
 * from the database. Useful if you need a copy of the tree for display purposes which is stable and
 * does not reflect any changes to the underlying real persistent nodes. This copy should be not
 * connected or hollow, that is, you should copy only the miminum properties you require for display
 * and probably not any collections or to-one entity references. However, it probably should copy the
 * one-to-one reference to the owner of the delegate, if that is what you are displaying.
 * </p>
 *
 * @author Christian Bauer
 */
public interface NestedSetDuplicator<N extends NestedSetNode> {

    /**
     * Make a (probably hollow) copy of the given nested set delegate.
     * <p>
     * You <b>have to</b> ensure that the copy holds the same identifier value as the original.
     * </p>
     * @param nestedSetNode the persistent nested set delegate from the database
     * @return null if a copy couln't be made, skipping the node in the tree result transformation
     */
    public N duplicate(N nestedSetNode);
}
