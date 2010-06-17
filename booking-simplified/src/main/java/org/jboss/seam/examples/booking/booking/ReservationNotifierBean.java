package org.jboss.seam.examples.booking.booking;

import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;
import org.jboss.seam.examples.booking.model.Booking;
import org.slf4j.Logger;

public class ReservationNotifierBean implements ReservationNotifier
{
   @Inject
   private Logger log;

   public void onBookingComplete(@Observes(during = TransactionPhase.AFTER_SUCCESS)
         @Confirmed final Booking booking)
   {
      log.info("In a real-world application, send e-mail containing reservation information to " + booking.getUser().getEmailWithName());
   }
}