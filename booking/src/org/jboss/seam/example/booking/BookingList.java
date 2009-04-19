//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface BookingList
{
   public void getBookings();
   public Booking getBooking();
   public void cancel();
   public void destroy();
}