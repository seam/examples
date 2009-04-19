/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.nestedset.query;

import org.jboss.seam.wiki.core.nestedset.NestedSetNode;

/**
 * Generate a simple query (or individual clauses for more complex queries) for nested set trees.
 * <p>
 * This is a convenience class, use it to build queries that return (sub)trees. The query build by
 * this class always returns a table with each tuple representing a node in the tree. The projected
 * aliases are, in order:
 * </p>
 * <ul>
 * <li><tt>nestedSetLevel</tt>, representing the level of the node in the tree
 * <li><tt>nestedSetDelegate</tt>, representing the nested set delegate, aka the node
 * </ul>
 * <p>
 * The <tt>owner</tt> of each nested set delegate is eagerly fetched, so you can access it immediately
 * after executing the query by calling <tt>nestedSetDelegate.getOwner()</tt> without the cost of an
 * additional database hit. The tree is ordered, that is, tuples that represent sub-nodes of a parent node
 * are always returned after the parent node.
 * </p>
 * <p>
 * A typical nested set query, for all subtree nodes starting at a particular node, looks as follows:
 * </p>
 * <pre>
 * NestedSetQueryBuilder nsQuery = new NestedSetQueryBuilder(startNode);
 * Query nestedSetQuery =  session.createQuery(nsQuery.getSimpleQuery());
 *
 * // Bind parameters
 * nestedSetQuery.setParameter("nsThread", startNode.getNestedSetDelegate().getNsThread());
 * nestedSetQuery.setParameter("nsLeft", startNode.getNestedSetDelegate().getNsLeft());
 * nestedSetQuery.setParameter("nsRight", startNode.getNestedSetDelegate().getNsRight());
 *
 * // Execute
 * List&lt;Object[]> tree = nestedSetQuery.list();
 *
 * // Iterate result
 * for (Object[] treeNodeTuple : tree) {
 *
 *     Long level = (Long)treeNodeTuple[0];
 *     NestedSetDelegate delegate = (NestedSetDelegate)treeNodeTuple[1];
 *
 *     // Real tree node
 *     NestedSetDelegateOwner node = delegate.getOwner();
 *     ...
 * }
 *
 * </pre>
 * <p>
 * The values for <tt>thread</tt>, <tt>startLeft</tt>, and <tt>startRight</tt> parameters are the values of
 * the root node of the subtree you want to query for. This start node is not included in the query result.
 * </p>
 * <p>
 * If you want to retrieve a tree not in tabular form but as a tree of nodes with parent/children collection
 * references, apply the {@link org.jboss.seam.wiki.core.nestedset.query.NestedSetResultTransformer} to the
 * query before executing it.
 * </p>
 * <p>
 * TODO: This class could rely on Hibernate interfaces and also do the parameter binding. It would also be
 * much nicer if we wouldn't have to group the properties of the delegate owner here, so we don't rely on
 * that interface at all.
 * </p>
 *
 *
 * @author Christian Bauer
 */
public class NestedSetQueryBuilder {

    public static String NODE_ALIAS = "n1";
    public static String NODE2_ALIAS = "n2";
    public static String PROJECTION_NODE_ALIAS = "nestedSetNode";
    public static String PROJECTION_LEVEL_ALIAS = "nestedSetLevel";

    NestedSetNode startTree;
    boolean restrictionIncludesStart = false;
    boolean fetchLazyProperties = false;

    public NestedSetQueryBuilder(NestedSetNode startTree) {
        this.startTree = startTree;
    }

    public NestedSetQueryBuilder(NestedSetNode startTree, boolean restrictionIncludesStart) {
        this.startTree = startTree;
        this.restrictionIncludesStart = restrictionIncludesStart;
    }

    public NestedSetQueryBuilder(NestedSetNode startTree, boolean restrictionIncludesStart, boolean fetchLazyProperties) {
        this.startTree = startTree;
        this.restrictionIncludesStart = restrictionIncludesStart;
        this.fetchLazyProperties = fetchLazyProperties;
    }

    public String getSelectNodeClause() {
        StringBuilder clause = new StringBuilder();
        clause.append(NODE_ALIAS).append(" as ").append(PROJECTION_NODE_ALIAS).append(" ");
        return clause.toString();
    }

    public String getSelectLevelClause() {
        StringBuilder clause = new StringBuilder();
        clause.append("count(").append(NestedSetQueryBuilder.NODE_ALIAS).append(".id) as ").append(PROJECTION_LEVEL_ALIAS);
        return clause.toString();
    }

    public String getFromClause() {
        StringBuilder clause = new StringBuilder();
        clause.append(getEntityName()).append(" ").append(NODE_ALIAS);
        if (fetchLazyProperties) clause.append(" fetch all properties");
        clause.append(", ").append(getEntityName()).append(" ").append(NODE2_ALIAS);
        return clause.toString();
    }

    public String getWhereClause() {
        return getWhereClause(true);
    }

    public String getWhereClause(boolean restrictOnNodeInfo) {
        StringBuilder clause = new StringBuilder();
        clause.append(NODE_ALIAS).append(".nodeInfo.nsThread = ").append(NODE2_ALIAS).append(".nodeInfo.nsThread");
        clause.append(" and ").append(NODE_ALIAS);
        clause.append(".nodeInfo.nsLeft between ");
        clause.append(NODE2_ALIAS).append(".nodeInfo.nsLeft and ").append(NODE2_ALIAS).append(".nodeInfo.nsRight");
        if (restrictOnNodeInfo) {
            clause.append(" and ").append(NODE2_ALIAS).append(".nodeInfo.nsThread = :nsThread");
            clause.append(" and ").append(NODE2_ALIAS).append(".nodeInfo.nsLeft ").append(restrictionIncludesStart ? ">=" : ">").append(" :nsLeft");
            clause.append(" and ").append(NODE2_ALIAS).append(".nodeInfo.nsRight ").append(restrictionIncludesStart ? "<=" : "< ").append(" :nsRight");
        }
        return clause.toString();
    }

    public String getGroupByClause() {
        StringBuilder clause = new StringBuilder();
        for (int i = 0; i < startTree.getPropertiesForGroupingInQueries().length; i++) {
            clause.append(NODE_ALIAS).append(".").append(startTree.getPropertiesForGroupingInQueries()[i]);
            if (i != startTree.getPropertiesForGroupingInQueries().length-1) clause.append(", ");
        }
        if (fetchLazyProperties) {
            clause.append(", ");
            for (int i = 0; i < startTree.getLazyPropertiesForGroupingInQueries().length; i++) {
                clause.append(NODE_ALIAS).append(".").append(startTree.getLazyPropertiesForGroupingInQueries()[i]);
                if (i != startTree.getLazyPropertiesForGroupingInQueries().length-1) clause.append(", ");
            }
        }
        clause.append(", ").append(NODE_ALIAS).append(".nodeInfo.nsLeft");
        clause.append(", ").append(NODE_ALIAS).append(".nodeInfo.nsRight");
        clause.append(", ").append(NODE_ALIAS).append(".nodeInfo.nsThread");
        clause.append(", ").append(NODE_ALIAS).append(".id");
        return clause.toString();
    }

    public String getOrderByClause() {
        StringBuilder clause = new StringBuilder();
        clause.append(NODE_ALIAS).append(".nodeInfo.nsLeft asc");
        return clause.toString();
    }

    public String getSimpleQuery() {
        StringBuilder query = new StringBuilder();
        query.append("select").append(" ");
        query.append(getSelectLevelClause()).append(", ");
        query.append(getSelectNodeClause()).append(" from ");
        query.append(getFromClause()).append(" where ");
        query.append(getWhereClause()).append(" group by ");
        query.append(getGroupByClause()).append(" order by ");
        query.append(getOrderByClause());
        return query.toString();
    }

    protected String getEntityName() {
        return startTree.getClass().getName();
    }

}
