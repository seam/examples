package org.jboss.seam.security.examples.authorization.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;

import org.jboss.seam.security.annotations.SecurityBindingType;

/**
 * @author Shane Bryzak
 */
@SecurityBindingType
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Foo {
    String bar();

    @Nonbinding String zzz() default "";
}
