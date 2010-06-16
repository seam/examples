/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * $Id$
 */
package org.jboss.seam.examples.booking.booking;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.Booking_;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.examples.booking.model.User_;
import org.jboss.seam.examples.booking.security.Identity;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.slf4j.Logger;

/**
 * @author Dan Allen
 */
@Named("bookingHistory")
@Stateful
@SessionScoped
public class BookingHistoryBean implements BookingHistory
{
   @Inject
   private Logger log;

   @PersistenceContext
   private EntityManager em;

   @Inject
   private Messages messages;

   @Inject
   private Identity identity;

   @Inject @Authenticated
   private Instance<User> currentUserInstance;

   private List<Booking> bookingsForUser = null;

   @Produces
   @Authenticated
   @Named("bookings")
   public List<Booking> getBookingsForCurrentUser()
   {
      if (bookingsForUser == null && identity.isLoggedIn())
      {
         fetchBookingsForCurrentUser();
      }
      return bookingsForUser;
   }

   public void afterBookingConfirmed(@Observes(during = TransactionPhase.AFTER_SUCCESS)
         @Confirmed final BookingEvent bookingEvent)
   {
      // optimization, save the db call
      if (bookingsForUser != null)
      {
         bookingsForUser.add(bookingEvent.getBooking());
      }
   }

   public void cancelBooking(final Booking selectedBooking)
   {
      log.info("Canceling booking {0} for {1}", selectedBooking.getId(), currentUserInstance.get().getName());
      Booking booking = em.find(Booking.class, selectedBooking.getId());
      if (booking != null)
      {
         em.remove(booking);
         messages.info(new BundleKey("messages", "booking.canceled")).textDefault("The booking at the {0} on {1,date} has been canceled.").textParams(selectedBooking.getHotel().getName(), selectedBooking.getCheckinDate());
      }
      else
      {
         messages.info(new BundleKey("messages", "booking.doesNotExist")).textDefault("Our records indicate that the booking you selected has already been canceled.");
      }

      bookingsForUser.remove(selectedBooking);
   }

   private void fetchBookingsForCurrentUser()
   {
      String username = currentUserInstance.get().getUsername();
      CriteriaBuilder builder = em.getCriteriaBuilder();
      CriteriaQuery<Booking> cquery = builder.createQuery(Booking.class);
      Root<Booking> booking = cquery.from(Booking.class);
      booking.fetch(Booking_.hotel, JoinType.INNER);
      cquery.select(booking)
            .where(builder.equal(booking.get(Booking_.user).get(User_.username), username))
            .orderBy(builder.asc(booking.get(Booking_.checkinDate)));

      bookingsForUser = em.createQuery(cquery).getResultList();
   }

}
