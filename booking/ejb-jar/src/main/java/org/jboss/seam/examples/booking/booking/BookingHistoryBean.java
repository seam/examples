package org.jboss.seam.examples.booking.booking;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Current;
import javax.enterprise.inject.Named;
import javax.enterprise.inject.Produces;
import javax.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

   // TODO should probably observe @AfterTransactionSuccess (but it is broken)
   public void afterBookingConfirmed(@Observes @Confirmed BookingEvent bookingEvent)
   {
      getBookingsForCurrentUser();
   }

   public void cancelBooking(Booking selectedBooking)
   {
      log.info("Canceling booking {0} for {1}", selectedBooking.getId(), user.getName());
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
