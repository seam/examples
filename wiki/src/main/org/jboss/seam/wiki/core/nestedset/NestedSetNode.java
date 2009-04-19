package org.jboss.seam.wiki.core.nestedset;

public interface NestedSetNode<N extends NestedSetNode> {

    public Long getId();
    public NestedSetNodeInfo<N> getNodeInfo();
    public NestedSetNodeInfo<N> getParentNodeInfo();

    /**
     * Utility method required until TODO: http://opensource.atlassian.com/projects/hibernate/browse/HHH-1615
     * is implemented. If you query for nested set subtrees, you need to GROUP BY all properties of
     * the implementor of this interface, including identifier, version, and foreign keys (many-to-one properties).
     * Yes, this is not great.
     *
     * @return all property names of scalar and foreign key properties of the nested set class hierarchy
     */
    public String[] getPropertiesForGroupingInQueries();
    public String[] getLazyPropertiesForGroupingInQueries();
    
}
