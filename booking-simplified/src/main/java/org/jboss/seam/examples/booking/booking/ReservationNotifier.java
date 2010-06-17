package org.jboss.seam.examples.booking.booking;

import org.jboss.seam.examples.booking.model.Booking;

public interface ReservationNotifier
{
   void onBookingComplete(Booking booking);
}
