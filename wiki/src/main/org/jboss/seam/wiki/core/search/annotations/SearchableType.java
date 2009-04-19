package org.jboss.seam.wiki.core.search.annotations;

/**
 * Enumeration of search types, influences UI and query building.
 * <p>
 * TODO: Refactor this into a type hierarchy that includes behavior (query building)
 * 
 * @author Christian Bauer
 */
public enum SearchableType {
    PHRASE, STRING, PASTDATE, NUMRANGE
}
