package org.jboss.seam.wiki.core.search.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Declare on entities or persistent properties to include them in the search feature.
 * <p>
 * If you place this annotation on an entity class that has also been indexed with
 * Hibernate Search, you will after startup find the entity searchable in the global
 * search mask with the given description. The <tt>type</tt> is ignored in this case.
 * <p>
 * If you place this annotation on a persistent property (field or getter method), the
 * property will be searchable individually as a search option. Use the appropriate
 * <tt>SearchableType</tt> in that case. The property name must be the same as the
 * name used for indexing. (TODO: make this more flexible)
 *
 * @author Christian Bauer
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Searchable {
    String description();
    SearchableType type() default SearchableType.PHRASE;
    String embeddedProperty() default "";
}
