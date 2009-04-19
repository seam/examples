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
 * Needs to be equals() safe (SortedSet), compare by name, if equal compare by id.
 *
 * @author Christian Bauer
 */
public class WikiDirectoryNameComparator
        implements Comparator<NestedSetNodeWrapper<WikiDirectory>>, Serializable {

    public int compare(NestedSetNodeWrapper<WikiDirectory> o1, NestedSetNodeWrapper<WikiDirectory> o2) {
        WikiDirectory node1 = o1.getWrappedNode();
        WikiDirectory node2 = o2.getWrappedNode();
        if (node1.getName().compareTo(node2.getName()) != 0) {
            return node1.getName().compareTo(node2.getName());
        }
        return node1.getId().compareTo(node2.getId());
    }

}
