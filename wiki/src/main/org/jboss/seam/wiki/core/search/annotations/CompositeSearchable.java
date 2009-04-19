package org.jboss.seam.wiki.core.search.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Group several searchable properties into one UI option.
 * <p>
 * The name of the properties must be the same as the indexed field names.
 *
 * @author Christian Bauer
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompositeSearchable {
    String description();
    SearchableType type() default SearchableType.PHRASE;
    String[] properties();
}
