//$Id: BookingList.java,v 1.7 2007/06/27 00:06:49 gavin Exp $
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