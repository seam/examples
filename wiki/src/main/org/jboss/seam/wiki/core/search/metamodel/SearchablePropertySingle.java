package org.jboss.seam.wiki.core.search.metamodel;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.jboss.seam.wiki.core.search.PropertySearch;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;

/**
 * A logical searchable property of a single indexed field.
 *
 * @author Christian Bauer
 */
public class SearchablePropertySingle extends SearchableProperty {

    private String name;

    public SearchablePropertySingle(String name, String description, SearchableType type) {
        super(description, type);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return getName();
    }

    public Query getQuery(PropertySearch search) {
        BooleanQuery query = new BooleanQuery();

        Query iq = buildIncludeQuery(getName(), search);
        if (iq != null) {
            log.debug("include query: " + iq.toString());
            query.add(iq, BooleanClause.Occur.MUST );
            Query eq= buildExcludeQuery(getName(), search);
            if (eq != null) {
                log.debug("exclude query: " + eq.toString());
                query.add(eq, BooleanClause.Occur.MUST_NOT);
            }
        }
        
        return query.getClauses().length > 0 ? query : null;
    }
}
