package org.jboss.seam.wiki.core.search;

import org.apache.lucene.search.*;
import org.apache.lucene.index.Term;
import org.hibernate.Hibernate;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.engine.DocumentBuilder;
import org.hibernate.search.bridge.StringBridge;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;
import org.jboss.seam.wiki.core.search.metamodel.SearchRegistry;
import org.jboss.seam.wiki.core.search.metamodel.SearchableEntity;
import org.jboss.seam.wiki.core.search.metamodel.SearchableProperty;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;

/**
 * Core search engine, coordinates the search UI, query building, and hit extraction.
 * <p>
 * This controller is the backend for two different UIs: A simple query input field that
 * is available on all pages, and the complete and complex search mask on the search page.
 *
 * @author Christian Bauer
 */
@Name("wikiSearch")
@Scope(ScopeType.CONVERSATION)
public class WikiSearch implements Serializable {

    public static final String FIELD_READACCESSLVL = "readAccessLevel";

    @Logger
    static Log log;

    @In
    protected EntityManager restrictedEntityManager;

    @In
    private SearchRegistry searchRegistry;

    // For UI binding to the global search field (and simplified search mask)
    private String simpleQuery = "Search...";
    private Boolean simpleQueryMatchExactPhrase;
    public String getSimpleQuery() { return simpleQuery; }
    public void setSimpleQuery(String simpleQuery) { this.simpleQuery = simpleQuery; }
    public Boolean getSimpleQueryMatchExactPhrase() { return simpleQueryMatchExactPhrase; }
    public void setSimpleQueryMatchExactPhrase(Boolean simpleQueryMatchExactPhrase) { this.simpleQueryMatchExactPhrase = simpleQueryMatchExactPhrase; }

    /// For UI binding of the complex search mask (with expanded options)
    private SearchableEntity selectedSearchableEntity;
    public SearchableEntity getSelectedSearchableEntity() { return selectedSearchableEntity; }
    public void setSelectedSearchableEntity(SearchableEntity selectedSearchableEntity) { this.selectedSearchableEntity = selectedSearchableEntity; }

    private Map<SearchableEntity, List<PropertySearch>> searches = new HashMap<SearchableEntity, List<PropertySearch>>();
    public Map<SearchableEntity, List<PropertySearch>> getSearches() { return searches; }
    public void setSearches(Map<SearchableEntity, List<PropertySearch>> searches) { this.searches = searches; }

    Set<SearchableEntity> searchEntities;

    private int totalCount;
    private int maxPageSize;
    private int pageSize;
    private int page;

    @Create
    public void create() {

        // TODO: Minor optimization, do this lazily when search.xhtml is rendered, not when the component is created (on every wiki page render)

        // Initialize the value holders used for UI binding
        for (SearchableEntity searchableEntity : searchRegistry.getSearchableEntities()) {
            log.trace("preparing search value holder for entity: " + searchableEntity.getDescription());

            List<PropertySearch> searchesForEntity = new ArrayList<PropertySearch>();
            for (SearchableProperty prop : searchableEntity.getProperties()) {
                log.trace("preparing search value holder for property: " + prop.getDescription());
                searchesForEntity.add(new PropertySearch(prop));
            }
            searches.put(searchableEntity, searchesForEntity);
        }

        pageSize = 15;
        maxPageSize = 100;
    }

    List<SearchHit> searchResult;

    public List<SearchHit> getSearchResult() {
        if (searchResult == null) search();
        return searchResult;
    }

    public void search() {
        page = 0;
        searchEntities = new TreeSet<SearchableEntity>();

        if (selectedSearchableEntity == null) {

            // Nothing selected, do a global search on all entities that support phrases and
            // use the simpleQuery as "include" search term for these phrases
            log.debug("global search on all entities with phrase-type properties");

            for (Map.Entry<SearchableEntity, List<PropertySearch>> entry : searches.entrySet()) {
                for (PropertySearch propertySearch : entry.getValue()) {
                    if (SearchableType.PHRASE.equals(propertySearch.getProperty().getType())) {
                        propertySearch.getTerms().put(SearchableProperty.TERM_INCLUDE, getSimpleQuery());
                        propertySearch.getTerms().put(SearchableProperty.TERM_EXCLUDE, "");
                        propertySearch.getTerms().put(SearchableProperty.TERM_MATCHEXACTPHRASE, getSimpleQueryMatchExactPhrase());
                        searchEntities.add(entry.getKey());
                    }
                    // And also simple string queries get the term
                    if (SearchableType.STRING.equals(propertySearch.getProperty().getType())) {
                        propertySearch.getTerms().put(SearchableProperty.TERM_INCLUDE, getSimpleQuery());
                    }
                }
            }

        } else {
            // Form with search details selected and filled out
            log.debug("searching only indexed entity: " + selectedSearchableEntity);
            searchEntities.add(selectedSearchableEntity);
        }

        executeSearch(searchEntities);

    }

    private void executeSearch(Set<SearchableEntity> searchableEntities) {

        log.debug("searching entities: " + searchableEntities.size());

        BooleanQuery mainQuery = new BooleanQuery();

        // Get value holders filled out by UI forms and generate a Lucene query
        for (SearchableEntity searchableEntity : searchableEntities) {
            log.debug("building query for entity: " + searchableEntity.getClazz());
            BooleanQuery entityQuery = new BooleanQuery();

            // Add restriction to entity clazz
            // We use a Hibernate Search internal constant here to limit THIS particular entity query
            // fragment to a particular indexed persistent entity type.
            log.debug("adding restriction to entity clazz: " + searchableEntity.getClazz().getName());
            entityQuery.add(
                new TermQuery(
                    new Term(DocumentBuilder.CLASS_FIELDNAME, searchableEntity.getClazz().getName())
                ),
                BooleanClause.Occur.MUST
            );

            // Add sub-queries for all entity properties
            BooleanQuery allPropertiesQuery = new BooleanQuery();
            for (PropertySearch search : searches.get(searchableEntity)) {
                log.debug("building query for property: " + search.getProperty());
                Query propertiesQuery  = search.getProperty().getQuery(search);
                if (propertiesQuery != null) {
                    // Any property can match, except if we are searching only one entity, then all must match
                    allPropertiesQuery.add(
                        propertiesQuery,
                        searchableEntities.size() == 1 ? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD
                    );
                }
            }

            // But SOME of the property searches for this entity must match
            log.debug("adding query to owning entity for properties: " + allPropertiesQuery.getClauses().length);
            entityQuery.add(allPropertiesQuery, BooleanClause.Occur.MUST);

            // Finally, figure out if this entity query needs to be read-restricted, we have indexed the readAccessLevel of it
            if (searchableEntity.getHandler().isReadAccessChecked()) {

                Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
                StringBridge paddingBridge = new PaddedIntegerBridge();
                Query accessLimitQuery =
                    new ConstantScoreRangeQuery(FIELD_READACCESSLVL, null, paddingBridge.objectToString(currentAccessLevel), true, true);
                Filter accessFilter = new QueryWrapperFilter(accessLimitQuery);
                FilteredQuery accessFilterQuery = new FilteredQuery(entityQuery, accessFilter);

                log.debug("adding filtered entity query to main query: " + accessFilterQuery);
                mainQuery.add(accessFilterQuery, BooleanClause.Occur.SHOULD);

            } else {
                log.debug("adding unfiltered entity query to main query: " + entityQuery);
                mainQuery.add(entityQuery, BooleanClause.Occur.SHOULD);
            }
        }

        log.debug(">>>>> search query: " + mainQuery.toString());

        try {

            FullTextQuery ftQuery = getFullTextSession().createFullTextQuery(mainQuery);
            ftQuery.setFirstResult(page * pageSize).setMaxResults(pageSize);
            totalCount = ftQuery.getResultSize();
            log.debug("total search hits (might be paginated next): " + totalCount);
            List result = ftQuery.list();

            // Extract hits
            log.debug("search hits passed to handlers: " + result.size());
            searchResult = new ArrayList<SearchHit>();
            for (Object o : result) {
                SearchableEntity se = searchRegistry.getSearchableEntitiesByName().get(Hibernate.getClass(o).getName());
                if (se != null) {
                    log.debug("extracting hit for indexed class: " + Hibernate.getClass(o).getName());
                    //noinspection unchecked
                    searchResult.add( se.getHandler().extractHit(mainQuery, o) );
                }
            }

            log.debug("extracted search hits and final result: " + searchResult.size());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private FullTextSession getFullTextSession() {
        return (FullTextSession) restrictedEntityManager.getDelegate();
    }

    public void nextPage() {
        page++;
        executeSearch(searchEntities);
    }

    public void previousPage() {
        page--;
        executeSearch(searchEntities);
    }

    public void firstPage() {
        page = 0;
        executeSearch(searchEntities);
    }

    public void lastPage() {
        page = (totalCount / pageSize);
        if (totalCount % pageSize == 0) page--;
        executeSearch(searchEntities);
    }

    public boolean isNextPageAvailable() {
        return totalCount > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return page > 0;
    }
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize > maxPageSize ? maxPageSize : pageSize; // Prevent tampering
    }

    public long getFirstRow() {
        return page * pageSize + 1;
    }

    public long getLastRow() {
        return (page * pageSize + pageSize) > totalCount
                ? totalCount
                : page * pageSize + pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
