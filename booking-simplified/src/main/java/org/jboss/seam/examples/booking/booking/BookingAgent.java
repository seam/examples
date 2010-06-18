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

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
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
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.TemplateMessage;
import org.slf4j.Logger;

import com.ocpsoft.pretty.time.PrettyTime;
import java.util.Locale;
import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.international.locale.UserLocale;

/**
 * @author Dan Allen
 */
@Named
@Stateful
@ConversationScoped
public class BookingAgent
{
   @Inject
   private Logger log;

   @PersistenceContext(type = EXTENDED)
   private EntityManager em;

   @Inject
   private Instance<TemplateMessage> messageBuilder;

   @Inject
   private Messages messages;

   @Inject
   @Authenticated
   private User user;

   @Inject
   private Locale locale;

   @Inject
   @Confirmed
   private Event<Booking> bookingConfirmedEventSrc;

   private Hotel hotelSelection;

   private Booking booking;

   private boolean bookingValid;

   @Begin
   public void selectHotel(final Long id)
   {
      // NOTE get a fresh reference that's managed by the extended persistence
      // context
      hotelSelection = em.find(Hotel.class, id);
      if (hotelSelection != null)
      {
         log.info(messageBuilder.get().text("Selected the {0} in {1}").textParams(hotelSelection.getName(), hotelSelection.getCity()).build().getText());
      }
   }

   public void bookHotel()
   {
      booking = new Booking(hotelSelection, user, 7, 2);
      hotelSelection = null;

      // for demo convenience
      booking.setCreditCardNumber("1111222233334444");

      messages.info(new DefaultBundleKey("booking.initiated")).textDefault("You've initiated a booking at the {0}.").textParams(booking.getHotel().getName());
   }

   public void validate()
   {
      // if we got here, all validations passed
      log.info("Does the persistence context still contain the hotel instance? " + em.contains(booking.getHotel()));
      bookingValid = true;
   }

   @End
   public void confirm()
   {
      em.persist(booking);
      bookingConfirmedEventSrc.fire(booking);
   }

   @End
   public void cancel()
   {
      booking = null;
      hotelSelection = null;
   }

   public void onBookingComplete(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Confirmed final Booking booking)
   {
      log.info(messageBuilder.get().text("New booking at the {0} confirmed for {1}").textParams(booking.getHotel().getName(), booking.getUser().getName()).build().getText());
      messages.info(new DefaultBundleKey("booking.confirmed")).textDefault("You're booked to stay at the {0} {1}.").textParams(booking.getHotel().getName(), new PrettyTime(locale).format(booking.getCheckinDate()));
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
   public Hotel getSelectedHotel()
   {
      return booking != null ? booking.getHotel() : hotelSelection;
   }

   public boolean isBookingValid()
   {
      return bookingValid;
   }
}
