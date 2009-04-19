/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset;

/**
 * Implemented by domain model classes if they support nested sets.
 * <p>
 * This is your starting point, add this interface to your domain model class and implement it.
 * </p>
 * <p>
 * You also need to implement a {@link org.jboss.seam.wiki.core.nestedset.NestedSetDelegate} class
 * for each domain model class that supports nested sets. You need to map a reference to this
 * delegate as follows:
 * </p>
 * <pre>
 * class MyDomainModelClass implements NestedSetDelegateOwner {
 *
 *     // Adjacency list for parent/child
 *     ...
 * 
 *     &#064;OneToOne(mappedBy="owner", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
 *     private MyDomainModelClassNestedSetDelegate nestedSetDelegate;
 * }
 * </pre>
 * <p>
 * Make sure that every instance of your domain model class has an instance of the delegate,
 * a <tt>@OneToOne</tt> to a persistent <tt>NestedSetDelegate</tt> instance (initialize it
 * in the constructor). In addition, you need to return the parents delegate (if there is any,
 * if not, then null) accordingly.
 * </p>
 *
 * @author Christian Bauer
 */
public interface NestedSetDelegateOwner {

    public Long getId();

    public NestedSetDelegate getNestedSetDelegate();
    public NestedSetDelegate getParentNestedSetDelegate();

    /**
     * Utility method required until TODO: http://opensource.atlassian.com/projects/hibernate/browse/HHH-1615
     * is implemented. If you query for nested set subtrees, you need to GROUP BY all properties of
     * the implementor of this interface, including identifier, version, and foreign keys (many-to-one properties).
     * Yes, this is not great.
     *
     * @return all property names of scalar and foreign key properties of the nested set class hierarchy
     */
    public String[] getPropertiesForGroupingInQueries();
}
