package org.jboss.seam.wiki.core.search.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * Declare several grouped searchable properties on an entity class.
 *
 * @author Christian Bauer
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompositeSearchables {
    CompositeSearchable[] value();
}

