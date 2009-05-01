package org.jboss.seam.examples.booking.booking;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Named;
import javax.annotation.PreDestroy;
import javax.context.RequestScoped;
import javax.context.SessionScoped;
import javax.ejb.Stateful;
import javax.event.Observes;
import javax.inject.Current;
import javax.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.booking.BookingEvent;
import org.jboss.seam.examples.booking.booking.Confirmed;
import org.jboss.seam.examples.booking.account.Registered;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.StatusMessages;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logger;

/**
 * @author Dan Allen
 */
public
@Named("bookingHistory")
@Stateful
@SessionScoped
class BookingHistoryBean implements BookingHistory {

   private @Logger Log log;

   @PersistenceContext EntityManager em;

   @Current StatusMessages statusMessages;

   @Registered User user;

   private List<Booking> bookingsForUser = new ArrayList<Booking>();

   public
   @Produces
   @Registered
   @Named("bookings")
   @SessionScoped
   List<Booking> getBookingsForCurrentUser()
   {
      bookingsForUser.clear();
      bookingsForUser.addAll(em.createQuery("select b from Booking b join fetch b.hotel where b.user.username = :username order by b.checkinDate")
         .setParameter("username", user.getUsername())
         .getResultList());
      return bookingsForUser;
   }

   // TODO should probably observe after transaction success
   public void afterBookingConfirmed(@Observes @Confirmed BookingEvent bookingEvent)
   {
      getBookingsForCurrentUser();
   }

   public void cancelBooking(Booking selectedBooking)
   {
      log.info("Canceling booking " + selectedBooking.getId() + " for " + user.getName());
      Booking booking = em.find(Booking.class, selectedBooking.getId());
      if (booking != null)
      {
         em.remove(booking);
         statusMessages.add("The booking at the {0} on {1,date} has been canceled.", selectedBooking.getHotel().getName(), selectedBooking.getCheckinDate());
      }
      else
      {
         statusMessages.add("Our records indicate that the booking you selected has already been canceled.");
      }

      bookingsForUser.remove(selectedBooking);
   }

   @PreDestroy
   public void destroy()
   {
   }

}
