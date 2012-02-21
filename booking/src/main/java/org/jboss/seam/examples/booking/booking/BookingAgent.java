/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.examples.booking.booking;

import java.util.Locale;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.ocpsoft.pretty.time.PrettyTime;
import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.log.BookingLog;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.faces.context.conversation.Begin;
import org.jboss.seam.faces.context.conversation.ConversationBoundaryInterceptor;
import org.jboss.seam.faces.context.conversation.End;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.TemplateMessage;
import org.jboss.solder.logging.TypedCategory;

import static javax.persistence.PersistenceContextType.EXTENDED;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Stateful
@ConversationScoped
@Named
@Interceptors(ConversationBoundaryInterceptor.class) // not necessary, this is a temporary workaround for GLASSFISH-17184
public class BookingAgent {
    @Inject
    @TypedCategory(BookingAgent.class)
    private BookingLog log;

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

    @Inject
    private Conversation conversation;

    @Begin
    public void selectHotel(final String id) {
        conversation.setTimeout(600000); //10 * 60 * 1000 (10 minutes)

        // NOTE get a fresh reference that's managed by the extended persistence context
        hotelSelection = em.find(Hotel.class, Long.valueOf(id));
        if (hotelSelection != null) {
            log.hotelSelected(user != null ? user.getName() : "Anonymous", hotelSelection.getName(), hotelSelection.getCity());
        }
    }

    public void bookHotel() {
        booking = new Booking(hotelSelection, user, 7, 2);
        hotelSelection = null;

        // for demo convenience
        booking.setCreditCardNumber("1111222233334444");
        log.bookingInitiated(user.getName(), booking.getHotel().getName());

        messages.info(new DefaultBundleKey("booking_initiated")).defaults("You've initiated a booking at the {0}.")
                .params(booking.getHotel().getName());
    }

    public void validate() {
        log.hotelEntityInPersistenceContext(em.contains(booking.getHotel()));
        // if we got here, all validations passed
        bookingValid = true;
    }

    @End
    public void confirm() {
        em.persist(booking);
        bookingConfirmedEventSrc.fire(booking);
    }

    @End
    public void cancel() {
        booking = null;
        hotelSelection = null;
    }

    public void onBookingComplete(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Confirmed final Booking booking) {
        log.bookingConfirmed(booking.getHotel().getName(), booking.getUser().getName());
        messages.info(new DefaultBundleKey("booking_confirmed")).defaults("You're booked to stay at the {0} {1}.")
                .params(booking.getHotel().getName(), new PrettyTime(locale).format(booking.getCheckinDate()));
    }

    @Produces
    @ConversationScoped
    @Named
    public Booking getBooking() {
        return booking;
    }

    @Produces
    @RequestScoped
    @Named("hotel")
    public Hotel getSelectedHotel() {
        return booking != null ? booking.getHotel() : hotelSelection;
    }

    public boolean isBookingValid() {
        return bookingValid;
    }
}
