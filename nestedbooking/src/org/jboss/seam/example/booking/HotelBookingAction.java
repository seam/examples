//$Id$
package org.jboss.seam.example.booking;
import static javax.persistence.PersistenceContextType.EXTENDED;
import java.util.Calendar;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;

@Stateful
@Name("hotelBooking")
@Restrict("#{identity.loggedIn}")
public class HotelBookingAction implements HotelBooking
{
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   @In 
   private User user;
   
   @In(required=false) @Out
   private Hotel hotel;
   
   @In(required=false) 
   @Out(required=false)
   private Booking booking;
   
   @In(required=false)
   private Room roomSelection;
   
   @In
   private FacesMessages facesMessages;
      
   @In
   private Events events;
   
   @Logger 
   private Log log;
   
   @Begin
   public void selectHotel(Hotel selectedHotel)
   {
      log.info("Selected hotel #0", selectedHotel.getName());
      hotel = em.merge(selectedHotel);
   }
   
   public String setBookingDates()
   {
      // the result will indicate whether or not to begin the nested conversation
      // as well as the navigation.  if a null result is returned, the nested
      // conversation will not begin, and the user will be returned to the current
      // page to fix validation issues
      String result = null;

      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -1);

      // validate what we have received from the user so far
      if ( booking.getCheckinDate().before( calendar.getTime() ) )
      {
         facesMessages.addToControl("checkinDate", "Check in date must be a future date");
      }
      else if ( !booking.getCheckinDate().before( booking.getCheckoutDate() ) )
      {
         facesMessages.addToControl("checkoutDate", "Check out date must be later than check in date");
      }
      else
      {
         result = "rooms";
      }

      return result;
   }
   
   public void bookHotel()
   {      
      booking = new Booking(hotel, user);
      Calendar calendar = Calendar.getInstance();
      booking.setCheckinDate( calendar.getTime() );
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      booking.setCheckoutDate( calendar.getTime() );
   }
   
   @End(root=true)
   public void confirm()
   {
      // on confirmation we set the room preference in the booking.  the room preference
      // will be injected based on the nested conversation we are in.
      booking.setRoomPreference(roomSelection);

      em.persist(booking);
      facesMessages.add("Thank you, #{user.name}, your confimation number for #{hotel.name} is #{booking.id}");
      log.info("New booking: #{booking.id} for #{user.username}");
      events.raiseTransactionSuccessEvent("bookingConfirmed");
   }
   
   @End(root=true, beforeRedirect=true)
   public void cancel() {}
   
   @Destroy @Remove
   public void destroy() {}
}
