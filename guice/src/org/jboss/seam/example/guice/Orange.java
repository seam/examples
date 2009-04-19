package org.jboss.seam.example.guice;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Indicates we want the orange version of a binding.
 *
 * @author Pawel Wrzeszcz (pwrzeszcz [at] jboss . org)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@BindingAnnotation
public @interface Orange
{
}
