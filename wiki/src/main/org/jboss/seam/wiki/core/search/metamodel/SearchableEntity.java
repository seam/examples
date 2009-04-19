package org.jboss.seam.wiki.core.search.metamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Meta-information about a particular entity that can be searched.
 * <p>
 * Provides global statistics properties which need to be set before they are read - this is
 * just a value holder.
 *
 * @author Christian Bauer
 */
public class SearchableEntity implements Serializable, Comparable {
    private Class clazz;
    private String description;
    private SearchableEntityHandler handler;
    private List<SearchableProperty> properties = new ArrayList<SearchableProperty>();

    public SearchableEntity(Class clazz) {
        this.clazz = clazz;
    }

    public SearchableEntity(Class clazz, String description, SearchableEntityHandler handler) {
        this.clazz = clazz;
        this.description = description;
        this.handler = handler;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getDescription() {
        return description;
    }

    public SearchableEntityHandler getHandler() {
        return handler;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchableEntity that = (SearchableEntity) o;
        if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;
        return true;
    }

    public int hashCode() {
        return (clazz != null ? clazz.hashCode() : 0);
    }

    public List<SearchableProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SearchableProperty> properties) {
        this.properties = properties;
    }

    // Some stats, used for admin screens
    private long numOfIndexedDocuments;
    private long numOfIndexedTerms;
    private long indexSizeInBytes;

    public long getNumOfIndexedDocuments() {
        return numOfIndexedDocuments;
    }

    public void setNumOfIndexedDocuments(long numOfIndexedDocuments) {
        this.numOfIndexedDocuments = numOfIndexedDocuments;
    }

    public long getNumOfIndexedTerms() {
        return numOfIndexedTerms;
    }

    public void setNumOfIndexedTerms(long numOfIndexedTerms) {
        this.numOfIndexedTerms = numOfIndexedTerms;
    }

    public long getIndexSizeInBytes() {
        return indexSizeInBytes;
    }

    public void setIndexSizeInBytes(long indexSizeInBytes) {
        this.indexSizeInBytes = indexSizeInBytes;
    }

    public int compareTo(Object o) {
        return getDescription().compareTo( ((SearchableEntity)o).getDescription() );
    }

    public String toString() {
        return getClazz().getName();
    }
}

