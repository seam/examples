package org.jboss.seam.examples.booking.reference;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.inject.BindingType;

/**
 * A binding type attached to a collection of years
 * indicating the set is limited to the possible
 * values for a credit card expiry date.
 *
 * @author Dan Allen
 */
public
@Target(
{
   METHOD, PARAMETER, FIELD
})
@Retention(RUNTIME)
@Documented
@BindingType
@Inherited
@interface CreditCardExpiryYears
{
}
