package org.jboss.seam.wiki.core.search;

import org.jboss.seam.wiki.core.search.metamodel.SearchableProperty;

import java.util.Map;
import java.util.HashMap;

/**
 * A value holder for UI binding.
 * <p>
 * Bound to the dynamic search mask user interface and used to transport user input values
 * into the search engine backend.
 *
 * @author Christian Bauer
 */
public class PropertySearch {

    private Map<String, Object> terms = new HashMap<String, Object>();
    private SearchableProperty property;

    public PropertySearch(SearchableProperty property) {
        this.property = property;
    }

    public Map<String, Object> getTerms() {
        return terms;
    }

    public void setTerms(Map<String, Object> terms) {
        this.terms = terms;
    }

    public SearchableProperty getProperty() {
        return property;
    }

}
