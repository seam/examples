package org.jboss.seam.example.booking;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;

import org.jboss.seam.example.booking.Room;

@Stateful
@Name("roomPreference")
@Restrict("#{identity.loggedIn}")
public class RoomPreferenceAction implements RoomPreference 
{

   @Logger 
   private Log log;

   @In private Hotel hotel;
   
   @In private Booking booking;

   @DataModel(value="availableRooms")
   private List<Room> availableRooms;

   @DataModelSelection(value="availableRooms")
   private Room roomSelection;
    
   @In(required=false, value="roomSelection")
   @Out(required=false, value="roomSelection")
   private Room room;

   @Factory("availableRooms")
   public void loadAvailableRooms()
   {
      availableRooms = hotel.getAvailableRooms(booking.getCheckinDate(), booking.getCheckoutDate());
      log.info("Retrieved #0 available rooms", availableRooms.size());
   }

   public BigDecimal getExpectedPrice()
   {
      log.info("Retrieving price for room #0", roomSelection.getName());
      
      return booking.getTotal(roomSelection);
   }

   @Begin(nested=true)
   public String selectPreference()
   {
      log.info("Room selected");
      
      this.room = this.roomSelection;
      
      return "payment";
   }

   public String requestConfirmation()
   {
      // all validations are performed through the s:validateAll, so checks are already
      // performed
      log.info("Request confirmation from user");
      
      return "confirm";
   }

   @End(beforeRedirect=true)
   public String cancel()
   {
      log.info("ending conversation");

      return "cancel";
   }

   @Destroy @Remove                                                                      
   public void destroy() {}	
}
