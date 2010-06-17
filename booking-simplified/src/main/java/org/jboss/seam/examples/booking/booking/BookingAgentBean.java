/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
 */
package org.jboss.seam.examples.booking.booking;

import static javax.persistence.PersistenceContextType.EXTENDED;

import java.util.Calendar;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.faces.context.conversation.Begin;
import org.jboss.seam.faces.context.conversation.End;
import org.jboss.seam.international.status.MessageFactory;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.slf4j.Logger;

import com.ocpsoft.pretty.time.PrettyTime;
import org.jboss.seam.examples.booking.Bundles;

@Named("bookingAgent")
@Stateful
@ConversationScoped
public class BookingAgentBean implements BookingAgent
{
   @Inject
   private Logger log;

   @PersistenceContext(type = EXTENDED)
   private EntityManager em;

   @Inject
   private MessageFactory msg;

   @Inject
   private Messages messages;

   @Inject
   @Authenticated
   private User user;

   // @Inject @Fires @Confirmed Event<BookingEvent> bookingConfirmedEvent;
   @Inject
   private BeanManager manager;

   private Hotel hotelSelection;

   private Booking booking;

   private boolean bookingValid;

   @Begin
   public void selectHotel(final Long id)
   {
      // NOTE get a fresh reference that's managed by the extended persistence context
      hotelSelection = em.find(Hotel.class, id);
      if (hotelSelection != null)
      {
         log.info(msg.info("Selected the {0} in {1}").textParams(hotelSelection.getName(), hotelSelection.getCity()).build().getText());
      }
   }

   public void bookHotel()
   {
      booking = new Booking(hotelSelection, user);
      // QUESTION push logic into Booking?
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      booking.setCheckinDate(calendar.getTime());
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      booking.setCheckoutDate(calendar.getTime());
      hotelSelection = null;
      messages.info(new BundleKey(Bundles.MESSAGES, "booking.initiated")).textDefault("You've initiated a booking at the {0}.").textParams(booking.getHotel().getName());
   }

   public void validate()
   {
      // if we got here, all validations passed
      bookingValid = true;
   }

   @End
   public void confirm()
   {
      em.persist(booking);
      // FIXME can't inject event object into bean with passivating scope
      manager.fireEvent(new BookingEvent(booking), ConfirmedLiteral.INSTANCE);
      log.info(msg.info("New booking at the {0} confirmed for {1}")
            .textParams(booking.getHotel().getName(), booking.getUser().getName()).build().getText());
      messages.info(new BundleKey(Bundles.MESSAGES, "booking.confirmed"))
            .textDefault("You're booked to stay at the {0} {1}.")
            .textParams(booking.getHotel().getName(), new PrettyTime().format(booking.getCheckinDate()));
   }

   @End
   public void cancel()
   {
      booking = null;
      hotelSelection = null;
   }

   @Produces
   @Named
   @ConversationScoped
   public Booking getBooking()
   {
      return booking;
   }

   @Produces
   @Named("hotel")
   @RequestScoped
   public Hotel getHotelSelection()
   {
      return booking != null ? booking.getHotel() : hotelSelection;
   }

   public boolean isBookingValid()
   {
      return bookingValid;
   }
}
