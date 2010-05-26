package org.jboss.seam.examples.booking.booking;

import org.jboss.seam.examples.booking.model.Booking;

/**
 * An event that is raised when a booking change occurs
 * (either a new booking is confirmed or an existing
 * booking is canceled).
 *
 * @author Dan Allen
 */
public class BookingEvent
{
   private Booking booking;

   public BookingEvent(Booking booking)
   {
      this.booking = booking;
   }

   public Booking getBooking()
   {
      return booking;
   }
}
