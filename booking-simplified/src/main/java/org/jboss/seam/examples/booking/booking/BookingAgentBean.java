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

import static javax.persistence.PersistenceContextType.EXTENDED;

import java.util.Calendar;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.AnnotationLiteral;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.account.Registered;
import org.jboss.seam.examples.booking.controls.BookingFormControls;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.StatusMessages;
import org.slf4j.Logger;

@Named("bookingAgent")
@Stateful
@ConversationScoped
public class BookingAgentBean implements BookingAgent
{
   @Inject private Logger log;

   @PersistenceContext(type = EXTENDED) private EntityManager em;

   @Inject private Conversation conversation;

   @Inject private StatusMessages statusMessages;

   @Inject private BookingFormControls formControls;

   @Inject @Registered private User user;

   //@Inject @Fires @Confirmed Event<BookingEvent> bookingConfirmedEvent;
   @Inject private BeanManager manager;

   private Hotel hotelSelection;

   private Booking booking;

   private boolean bookingValid;

   public void selectHotel(Hotel hotel)
   {
	   // NOTE get a fresh reference that's managed by the conversational persistence context
      hotelSelection = em.find(Hotel.class, hotel.getId());
      log.info("Selected the {0} in {1}", hotelSelection.getName(), hotelSelection.getCity());
      conversation.begin();
   }

   public void bookHotel()
   {
      booking = new Booking(hotelSelection, user);
      // QUESTION push logic into Booking?
      Calendar calendar = Calendar.getInstance();
      booking.setCheckinDate(calendar.getTime());
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      booking.setCheckoutDate(calendar.getTime());
	   hotelSelection = null;
      statusMessages.addFromResourceBundleOrDefault("booking.initiated", "You've initiated a booking at {0}.", booking.getHotel().getName());
   }

   public void validateBooking()
   {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      if (booking.getCheckinDate().before(calendar.getTime()))
      {
         statusMessages.addToControlFromResourceBundleOrDefault(formControls.getCheckinDateControlId(), "booking.checkInNotFutureDate", "Check in date must be a future date");
         bookingValid = false;
      }
      else if (!booking.getCheckinDate().before(booking.getCheckoutDate()))
      {
         statusMessages.addToControlFromResourceBundleOrDefault(formControls.getCheckoutDateControlId(), "booking.checkOutBeforeCheckIn", "Check out date must be after check in date");
         bookingValid = false;
      }
      else
      {
         bookingValid = true;
      }
   }

   public void confirm()
   {
      em.persist(booking);
      // FIXME can't inject event object into bean with passivating scope
      //bookingConfirmedEvent.fire(new BookingEvent(booking));
      manager.fireEvent(new BookingEvent(booking), new AnnotationLiteral<Confirmed>() {});
      log.info("New booking at the {0} confirmed for {1}", booking.getHotel().getName(), booking.getUser().getName());
      statusMessages.addFromResourceBundleOrDefault("booking.confirmed", "Booking confirmed.");
      conversation.end();
   }

   public void cancel()
   {
      booking = null;
      hotelSelection = null;
      conversation.end();
   }

   public
   @Produces
   @Named
   @ConversationScoped
   Booking getBooking()
   {
      return booking;
   }

   public
   @Produces
   @Named("hotel")
   @RequestScoped
   Hotel getHotelSelection()
   {
      return booking != null ? booking.getHotel() : hotelSelection;
   }

   public boolean isBookingValid()
   {
      return bookingValid;
   }

   @Remove
   public void destroy()
   {
   }
}
