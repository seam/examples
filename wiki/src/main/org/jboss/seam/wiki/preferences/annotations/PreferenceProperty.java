package org.jboss.seam.wiki.preferences.annotations;

import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PreferenceProperty {
    String description();
    PreferenceVisibility[] visibility() default PreferenceVisibility.SYSTEM;
    String editorIncludeName() default "";
    String templateComponentName() default "";
    String mappedTo() default "";
}