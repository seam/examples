package org.jboss.seam.examples.booking.booking;

import java.util.List;
import javax.ejb.Local;
import org.jboss.seam.examples.booking.model.Booking;

/**
 * @author Dan Allen
 */
@Local
public interface BookingHistory
{
   List<Booking> getBookingsForCurrentUser();

   void cancelBooking(Booking booking);

   void afterBookingConfirmed(BookingEvent bookingEvent);
}
