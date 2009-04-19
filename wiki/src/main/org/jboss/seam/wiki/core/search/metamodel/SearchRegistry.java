package org.jboss.seam.wiki.core.search.metamodel;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.search.annotations.*;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.beans.Introspector;

/**
 * Runs on startup and reads all <tt>@Searchable</tt> entities.
 * <p>
 * Extracts all annotated entities and properties and builds a registry of metadata that is
 * used by the search engine to render the UI, build queries, extract hits, etc.
 * <p>
 * TODO: Once the Seam scanner is public, all of this can be simplified and
 * even the SearchableEntityHandlers can be declared with annotations
 *
 * @author Christian Bauer
 */
@Name("searchRegistry")
@Scope(ScopeType.APPLICATION)
public class SearchRegistry {

    @Logger
    static Log log;

    Map<String, SearchableEntity> searchableEntitiesByName = new HashMap<String, SearchableEntity>();
    List<SearchableEntity> searchableEntities = new ArrayList<SearchableEntity>();

    @Observer("Wiki.startup")
    public void scanForSearchSupportComponents() {

        log.debug("initializing search registry");
        searchableEntities.clear();
        searchableEntitiesByName.clear();

        // Fire an event and let all listeners add themself into the given collection
        Set<SearchSupport> searchSupportComponents = new HashSet<SearchSupport>();
        Events.instance().raiseEvent("Search.addSearchSupport", searchSupportComponents);

        log.debug("found search support components: " + searchSupportComponents.size());

        for (SearchSupport component : searchSupportComponents) {

            for (SearchableEntityHandler searchableEntityHandler : component.getSearchableEntityHandlers()) {
                SearchableEntity searchableEntity =  extractSearchableEntity(searchableEntityHandler);
                searchableEntities.add(searchableEntity);
                searchableEntitiesByName.put(searchableEntity.getClazz().getName(), searchableEntity);
            }
        }

        // Sort entities
        Collections.sort(searchableEntities);

        log.debug("done extracting metadata for searchable entities: " + searchableEntities.size());
    }

    private SearchableEntity extractSearchableEntity(SearchableEntityHandler handler) {
        Class<?> entityClass = handler.getSearchableEntityClass();

        if (!entityClass.isAnnotationPresent(Searchable.class) ||
            !entityClass.isAnnotationPresent(org.hibernate.search.annotations.Indexed.class)) {
            throw new RuntimeException("Configured as searchable but missing @Searchable and/or @Indexed: " + entityClass.getName());
        }

        log.debug("extracting entity search information from: " + entityClass.getName());
        // Extract entity information
        String entityDescription = entityClass.getAnnotation(Searchable.class).description();
        SearchableEntity searchableEntity = new SearchableEntity(entityClass, entityDescription, handler);

        // Extract composite property information
        if (entityClass.isAnnotationPresent(CompositeSearchable.class)) {
            searchableEntity.getProperties().add(
                extractCompositeSearchable(entityClass, entityClass.getAnnotation(CompositeSearchable.class))
            );
        }
        if (entityClass.isAnnotationPresent(CompositeSearchables.class)) {
            for (CompositeSearchable compositeSearchable : entityClass.getAnnotation(CompositeSearchables.class).value()) {
                searchableEntity.getProperties().add(extractCompositeSearchable(entityClass, compositeSearchable));
            }
        }

        // Extract property information
        String propertyName;
        String indexFieldName;
        String propertyDescription;
        SearchableType type;

        // @Searchable getter methods
        for (Method method : getGetters(entityClass, Searchable.class, org.hibernate.search.annotations.Field.class)) {

            indexFieldName = method.getAnnotation(org.hibernate.search.annotations.Field.class).name();
            propertyName = Introspector.decapitalize(method.getName().substring(3));
            propertyDescription = method.getAnnotation(Searchable.class).description();
            type = method.getAnnotation(Searchable.class).type();

            SearchableProperty property = new SearchablePropertySingle(
                    indexFieldName != null && indexFieldName.length() > 0 ? indexFieldName : propertyName,
                    propertyDescription,
                    type
            );
            searchableEntity.getProperties().add(property);
        }

        // @Searchable and embedded indexed getters
        for (Method method : getGetters(entityClass, Searchable.class, org.hibernate.search.annotations.IndexedEmbedded.class)) {

            String prefix = method.getAnnotation(org.hibernate.search.annotations.IndexedEmbedded.class).prefix();
            propertyName = prefix + method.getAnnotation(Searchable.class).embeddedProperty();
            if (propertyName.length() == 0)
                throw new RuntimeException("@IndexedEmbedded requires @Searchable(embeddedProperty) name on entity " + entityClass.getName());
            propertyDescription = method.getAnnotation(Searchable.class).description();
            type = method.getAnnotation(Searchable.class).type();

            SearchableProperty property = new SearchablePropertySingle(
                    propertyName,
                    propertyDescription,
                    type
            );
            searchableEntity.getProperties().add(property);
        }

        // @Searchable fields
        for (Field field : getFields(entityClass, Searchable.class, org.hibernate.search.annotations.Field.class)) {
            indexFieldName = field.getAnnotation(org.hibernate.search.annotations.Field.class).name();
            propertyName = field.getName();
            propertyDescription = field.getAnnotation(Searchable.class).description();
            type = field.getAnnotation(Searchable.class).type();

            SearchableProperty property = new SearchablePropertySingle(
                    indexFieldName != null && indexFieldName.length() > 0? indexFieldName : propertyName,
                    propertyDescription,
                    type
            );
            searchableEntity.getProperties().add(property);

        }

        // @Searchable and embedded indexed fields
        for (Field field : getFields(entityClass, Searchable.class, org.hibernate.search.annotations.IndexedEmbedded.class)) {
            String prefix = field.getAnnotation(org.hibernate.search.annotations.IndexedEmbedded.class).prefix();
            propertyName = prefix + field.getAnnotation(Searchable.class).embeddedProperty();
            if (propertyName.length() == 0)
                throw new RuntimeException("@IndexedEmbedded requires @Searchable(embeddedProperty) name on entity " + entityClass.getName());
            propertyDescription = field.getAnnotation(Searchable.class).description();
            type = field.getAnnotation(Searchable.class).type();

            SearchableProperty property = new SearchablePropertySingle(
                    propertyName,
                    propertyDescription,
                    type
            );
            searchableEntity.getProperties().add(property);

        }

        return searchableEntity;
    }

    private SearchablePropertyComposite extractCompositeSearchable(Class entityClass, CompositeSearchable compositeSearchable) {

        // Get all fields/getter methods with a Hibernate Search @Field annotation
        Set<String> searchableProperties = new HashSet<String>();

        for (Method method : getGetters(entityClass, org.hibernate.search.annotations.Field.class)) {
            String indexFieldName = method.getAnnotation(org.hibernate.search.annotations.Field.class).name();
            String propertyName = Introspector.decapitalize(method.getName().substring(3));
            searchableProperties.add(
                indexFieldName != null && indexFieldName.length() > 0? indexFieldName : propertyName
            );
        }
        for (Field field : getFields(entityClass, org.hibernate.search.annotations.Field.class)) {
            String propertyName = field.getName();
            String indexFieldName = field.getAnnotation(org.hibernate.search.annotations.Field.class).name();
            searchableProperties.add(
                indexFieldName != null && indexFieldName.length() > 0? indexFieldName : propertyName
            );

        }

        // Validate configured composite property names against fields/getters
        for (String s : compositeSearchable.properties()) {
            if (!searchableProperties.contains(s)) {
                throw new RuntimeException(
                    "No indexed field/getter could be found for " +
                    "configured searchable composite property '" + s + "' in entity: " + entityClass.getName());
            }
        }

        return new SearchablePropertyComposite(
                compositeSearchable.properties(),
                compositeSearchable.description(),
                compositeSearchable.type()
        );
    }

    private Method[] getGetters(Class clazz, Class<? extends Annotation>... requiredAnnotations) {
        List<Method> allMethods = new ArrayList<Method>();
        Class c = clazz;
        do {
            Method[] methods = c.getDeclaredMethods();
            for (Method method : methods) {

                boolean annotationsPresent = true;
                for (Class<? extends Annotation> requiredAnnotation : requiredAnnotations) {
                    if (!method.isAnnotationPresent(requiredAnnotation)) annotationsPresent = false;
                }

                if (annotationsPresent &&
                    method.getName().startsWith("get") &&
                    method.getParameterTypes().length == 0) {

                    allMethods.add(method);
                    if (!method.isAccessible()) method.setAccessible(true);
                }
            }
            c = c.getSuperclass();
        } while (c != Object.class);
        return allMethods.toArray(new Method[allMethods.size()]);
    }

    private Field[] getFields(Class clazz, Class<? extends Annotation>... requiredAnnotations) {
        List<Field> allFields = new ArrayList<Field>();
        Class c = clazz;
        do {
            Field[] fields = c.getDeclaredFields();
            for (Field field : fields) {

                boolean annotationsPresent = true;
                for (Class<? extends Annotation> requiredAnnotation : requiredAnnotations) {
                    if (!field.isAnnotationPresent(requiredAnnotation)) annotationsPresent = false;
                }

                if (annotationsPresent) {
                    allFields.add(field);
                    if (!field.isAccessible()) field.setAccessible(true);
                }
            }
            c = c.getSuperclass();
        } while (c != Object.class);
        return allFields.toArray(new Field[allFields.size()]);
    }

    public Map<String, SearchableEntity> getSearchableEntitiesByName() {
        return searchableEntitiesByName;
    }

    public List<SearchableEntity> getSearchableEntities() {
        return searchableEntities;
    }
}
