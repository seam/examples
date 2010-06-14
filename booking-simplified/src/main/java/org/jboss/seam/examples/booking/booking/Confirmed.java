package org.jboss.seam.examples.booking.booking;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

/**
 * @author Dan Allen
 */
@Target( { TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface Confirmed
{

   static class ConfirmedLiteral extends AnnotationLiteral<Confirmed> implements Confirmed
   {
      private static final long serialVersionUID = -7035985583479407806L;
   }
   
   public static final Confirmed INSTANCE = new ConfirmedLiteral();

}
