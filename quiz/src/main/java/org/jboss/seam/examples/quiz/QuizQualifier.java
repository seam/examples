package org.jboss.seam.examples.quiz;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QuizQualifier {

    String value();
}
