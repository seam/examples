package org.jboss.seam.examples.booking.booking;

import javax.enterprise.util.AnnotationLiteral;

public class ConfirmedLiteral extends AnnotationLiteral<Confirmed> implements Confirmed
{
   public static final ConfirmedLiteral INSTANCE = new ConfirmedLiteral();
}
