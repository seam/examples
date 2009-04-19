package org.jboss.seam.example.seampay;

import org.hibernate.validator.*;

import java.lang.annotation.*;

@ValidatorClass(DigitsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD}) 
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Digits {
    int integerDigits();
    int fractionalDigits() default 0;
    String message() default "invalid numeric value";
}
