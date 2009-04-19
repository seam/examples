package org.jboss.seam.wiki.core.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.DisplayTagCount;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetQueryBuilder;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

@Name("tagDAO")
@AutoCreate
public class TagDAO {

    @Logger
    static Log log;

    @In
    protected EntityManager restrictedEntityManager;

    // TODO: This query needs to be optimized, the nested subselect with in() is not good for MySQL, needs to be a join
    public List<DisplayTagCount> findTagCounts(WikiDirectory startDir, WikiFile ignoreFile, int limit, long minimumCount) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select t as tag, count(t) as count").append(" ");
        queryString.append("from WikiFile f join f.tags as t").append(" ");
        queryString.append("where f.parent.id in");
        queryString.append("(").append(getNestedDirectoryQuery(startDir)).append(")").append(" ");
        if (ignoreFile != null && ignoreFile.getId() != null) queryString.append("and not f = :ignoreFile").append(" ");
        queryString.append("group by t").append(" ");
        queryString.append("having count(t) >= :minimumCount").append(" ");
        queryString.append("order by count(t) desc, t asc ");

        Query nestedSetQuery = getSession().createQuery(queryString.toString());
        nestedSetQuery.setParameter("nsThread", startDir.getNodeInfo().getNsThread());
        nestedSetQuery.setParameter("nsLeft", startDir.getNodeInfo().getNsLeft());
        nestedSetQuery.setParameter("nsRight", startDir.getNodeInfo().getNsRight());
        nestedSetQuery.setParameter("minimumCount", minimumCount);
        if (ignoreFile != null && ignoreFile.getId() != null)
            nestedSetQuery.setParameter("ignoreFile", ignoreFile);
        if (limit > 0) {
            nestedSetQuery.setMaxResults(limit);
        }

        nestedSetQuery.setResultTransformer(Transformers.aliasToBean(DisplayTagCount.class));

        return nestedSetQuery.list();
    }

    public List<WikiFile> findWikFiles(WikiDirectory startDir, WikiFile ignoreFile, final String tag,
                                       WikiNode.SortableProperty orderBy, boolean orderAscending) {

        if (tag == null || tag.length() == 0) return Collections.EMPTY_LIST;

        StringBuilder queryString = new StringBuilder();

        queryString.append("select distinct f from WikiFile f join f.tags as t where f.parent.id in");
        queryString.append("(").append(getNestedDirectoryQuery(startDir)).append(")").append(" ");
        if (ignoreFile != null && ignoreFile.getId() != null) queryString.append("and not f = :ignoreFile").append(" ");
        queryString.append("and t = :tag").append(" ");
        queryString.append("order by f.").append(orderBy.name()).append(" ").append(orderAscending ? "asc" : "desc");

        Query nestedSetQuery = getSession().createQuery(queryString.toString());
        nestedSetQuery.setParameter("nsThread", startDir.getNodeInfo().getNsThread());
        nestedSetQuery.setParameter("nsLeft", startDir.getNodeInfo().getNsLeft());
        nestedSetQuery.setParameter("nsRight", startDir.getNodeInfo().getNsRight());
        if (ignoreFile != null && ignoreFile.getId() != null)
            nestedSetQuery.setParameter("ignoreFile", ignoreFile);
        nestedSetQuery.setParameter("tag", tag);

        return nestedSetQuery.list();
    }

    private String getNestedDirectoryQuery(WikiDirectory dir) {
        NestedSetQueryBuilder builder = new NestedSetQueryBuilder(dir, true);
        StringBuilder queryString = new StringBuilder();
        queryString.append("select distinct ").append(NestedSetQueryBuilder.NODE_ALIAS).append(".id").append(" ");
        queryString.append("from ").append(builder.getFromClause()).append(" ");
        queryString.append("where ").append(builder.getWhereClause()).append(" ");
        return queryString.toString();
    }

    private Session getSession() {
        return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
    }


}
