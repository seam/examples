package org.jboss.seam.example.booking;

import java.math.BigDecimal;

import javax.ejb.Local;

@Local
public interface RoomPreference 
{
   
   public void loadAvailableRooms();

   public String selectPreference();

   public BigDecimal getExpectedPrice();

   public String requestConfirmation();

   public String cancel();

   public void destroy();

}
