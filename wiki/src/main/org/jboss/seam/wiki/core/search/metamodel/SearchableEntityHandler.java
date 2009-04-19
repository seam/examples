package org.jboss.seam.wiki.core.search.metamodel;

import org.apache.lucene.search.Query;
import org.jboss.seam.wiki.core.search.SearchHit;

import java.lang.reflect.ParameterizedType;

/**
 * Superclass of a search handler for a particular searchable entity.
 * <p>
 * Extend this class to complete the search functionality for a particular entity, in
 * addition to placing <tt>@Searchable</tt> annotations on it. This handler extracts
 * hits from a search result (does highlighting, fragmentation, etc.) and supports
 * other dynamically overridable metadata (not much in this first incarnation).
 *
 * @author Christian Bauer
 */
public abstract class SearchableEntityHandler<E> {

    private Class<E> searchableEntityClass;

    public SearchableEntityHandler() {
        //noinspection unchecked
        this.searchableEntityClass = (Class<E>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<E> getSearchableEntityClass() {
        return searchableEntityClass;
    }

    public boolean isReadAccessChecked() { return false; }

    public abstract SearchHit extractHit(Query query, E hit) throws Exception;

}
