//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface HotelBooking
{
   public void selectHotel(Hotel selectedHotel);
   
   public void bookHotel();
   
   public void setBookingDetails();
   public boolean isBookingValid();
   
   public void confirm();
   
   public void cancel();
   
   public void destroy();
   
}