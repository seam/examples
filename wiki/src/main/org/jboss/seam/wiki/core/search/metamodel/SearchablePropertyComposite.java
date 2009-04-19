package org.jboss.seam.wiki.core.search.metamodel;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.jboss.seam.wiki.core.search.PropertySearch;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;

/**
 * A logical searchable property that is a composite of several indexed fields.
 *
 * @author Christian Bauer
 */
public class SearchablePropertyComposite extends SearchableProperty {

    private String[] names;

    public SearchablePropertyComposite(String[] names, String description, SearchableType type) {
        super(description, type);
        this.names = names;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public String toString() {
        String name = "Composite: ";
        for (String s : getNames()) {
            name += s + " ";
        }
        return name;
    }

    public Query getQuery(PropertySearch search) {
        BooleanQuery query = new BooleanQuery();

        BooleanQuery includeQuery = new BooleanQuery();
        BooleanQuery excludeQuery = new BooleanQuery();

        for (String s : getNames()) {
            Query iq = buildIncludeQuery(s, search);
            if (iq != null) {
                log.debug("include query: " + iq.toString());
                includeQuery.add(iq, BooleanClause.Occur.SHOULD);
                Query eq = buildExcludeQuery(s, search);
                if (eq != null) {
                    log.debug("exclude query: " + eq.toString());
                    excludeQuery.add(eq, BooleanClause.Occur.SHOULD);
                }
            }
        }

        if (includeQuery.getClauses().length > 0) query.add(includeQuery, BooleanClause.Occur.MUST);
        if (excludeQuery.getClauses().length > 0) query.add(excludeQuery, BooleanClause.Occur.MUST_NOT);
        return query.getClauses().length > 0 ? query : null;
    }

}
