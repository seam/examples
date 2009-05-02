package org.jboss.seam.examples.booking.booking;

import java.util.List;
import javax.ejb.Local;
import org.jboss.seam.examples.booking.model.Booking;

/**
 * @author Dan Allen
 */
public
@Local
interface BookingHistory
{
   List<Booking> getBookingsForCurrentUser();

   void cancelBooking(Booking booking);

   void destroy();

   void afterBookingConfirmed(BookingEvent bookingEvent);
}
