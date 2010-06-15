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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.account.Registered;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.User;
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
   @Registered
   private User user;

   private final List<Booking> bookingsForUser = new ArrayList<Booking>();

   @Produces
   @Registered
   @Named("bookings")
   @SessionScoped
   public List<Booking> getBookingsForCurrentUser()
   {
      bookingsForUser.clear();
      bookingsForUser.addAll(em.createQuery("select b from Booking b join fetch b.hotel where b.user.username = :username order by b.checkinDate").setParameter("username", user.getUsername()).getResultList());
      return bookingsForUser;
   }

   // TODO should probably observe @AfterTransactionSuccess (but it is broken)
   public void afterBookingConfirmed(@Observes @Confirmed final BookingEvent bookingEvent)
   {
      getBookingsForCurrentUser();
   }

   public void cancelBooking(final Booking selectedBooking)
   {
      log.info("Canceling booking {0} for {1}", selectedBooking.getId(), user.getName());
      Booking booking = em.find(Booking.class, selectedBooking.getId());
      if (booking != null)
      {
         em.remove(booking);
         messages.info(new BundleKey("messages.properties", "booking.canceled")).textDefault("The booking at the {0} on {1,date} has been canceled.").textParams(selectedBooking.getHotel().getName(), selectedBooking.getCheckinDate());
      }
      else
      {
         messages.info(new BundleKey("messages.properties", "booking.doesNotExist")).textDefault("Our records indicate that the booking you selected has already been canceled.");
      }

      bookingsForUser.remove(selectedBooking);
   }

   @PreDestroy
   public void destroy()
   {
   }

}
