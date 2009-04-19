package org.jboss.seam.wiki.util;

import java.util.List;

/**
 * A list of items, optionally filtered by search prefix, one might be selected.
 *
 * @author Christian Bauer
 */
public class FilteringSelector<T> {

    protected String filterLabel;
    protected List<T> items;
    protected T selected;
    protected String searchPrefix;

    public FilteringSelector(String filterLabel) {
        this.filterLabel = filterLabel;
        searchPrefix = filterLabel;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public T getSelected() {
        return selected;
    }

    public void setSelected(T selected) {
        this.selected = selected;
    }

    public String getSearchPrefix() {
        return searchPrefix;
    }

    public void setSearchPrefix(String searchPrefix) {
        this.searchPrefix = searchPrefix;
    }

    public void reset() {
        searchPrefix = filterLabel;
    }

    public String getSearchPrefixClean() {
        return searchPrefix == null ||
               searchPrefix.length() == 0 ||
               searchPrefix.equals(filterLabel)
                ? null
                : searchPrefix;
    }

}
