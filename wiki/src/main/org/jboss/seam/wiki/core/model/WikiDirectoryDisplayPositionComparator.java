/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Needs to be equals() safe (SortedSet): compare by display position, if equal compare by name,
 * if equal compare by id.
 *
 * @author Christian Bauer
 */
public class WikiDirectoryDisplayPositionComparator
        implements Comparator<NestedSetNodeWrapper<WikiDirectory>>, Serializable {

    public int compare(NestedSetNodeWrapper<WikiDirectory> o1, NestedSetNodeWrapper<WikiDirectory> o2) {
        WikiDirectory node1 = o1.getWrappedNode();
        Long node1DisplayPosition = (Long)o1.getAdditionalProjections().get("displayPosition");
        WikiDirectory node2 = o2.getWrappedNode();
        Long node2DisplayPosition = (Long)o2.getAdditionalProjections().get("displayPosition");
        if (node1DisplayPosition.compareTo(node2DisplayPosition) != 0) {
            return node1DisplayPosition.compareTo(node2DisplayPosition);
        } else if (node1.getName().compareTo(node2.getName()) != 0) {
            return node1.getName().compareTo(node2.getName());
        }
        return node1.getId().compareTo(node2.getId());
    }

}
