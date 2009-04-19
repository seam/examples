/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui.icon;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.WikiNode;

/**
 * @author Christian Bauer
 */
@Scope(ScopeType.CONVERSATION)
public abstract class IconHandler<N extends WikiNode> {

    public abstract String getIconName(N wikiNode);

}
