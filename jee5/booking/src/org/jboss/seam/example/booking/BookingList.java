//$Id: BookingList.java,v 1.1 2007/06/23 18:33:59 pmuir Exp $
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