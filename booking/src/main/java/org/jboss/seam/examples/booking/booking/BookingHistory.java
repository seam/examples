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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
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

import org.jboss.logging.Logger;
import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.Booking_;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.examples.booking.model.User_;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;

/**
 * The booking history exposes the current users existing bookings
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Stateful
@SessionScoped
@Named
public class BookingHistory {
    @Inject
    private Logger log;

    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private Messages messages;

    @Inject
    private Identity identity;

    @Inject
    @Authenticated
    private Instance<User> currentUserInstance;

    private List<Booking> bookingsForUser = null;

    @Produces
    @Authenticated
    @Named("bookings")
    public List<Booking> getBookingsForCurrentUser() {
        if (bookingsForUser == null && identity.isLoggedIn()) {
            fetchBookingsForCurrentUser();
        }
        return bookingsForUser;
    }

    public void onBookingComplete(
            @Observes(during = TransactionPhase.AFTER_SUCCESS, notifyObserver = Reception.IF_EXISTS) @Confirmed Booking booking) {
        // optimization, save the db call
        if (bookingsForUser != null) {
            log.info("Adding new booking to user's cached booking history");
            bookingsForUser.add(booking);
        } else {
            log.info("User's booking history not loaded. Skipping cache update.");

        }
    }

    public void cancelBooking(final Booking selectedBooking) {
        log.infov("Canceling booking {0} for {1}", selectedBooking.getId(), currentUserInstance.get().getName());
        Booking booking = entityManager.find(Booking.class, selectedBooking.getId());
        if (booking != null) {
            entityManager.remove(booking);
            messages.info(new DefaultBundleKey("booking_canceled"))
                    .defaults("The booking at the {0} on {1} has been canceled.")
                    .params(selectedBooking.getHotel().getName(),
                            DateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(selectedBooking.getCheckinDate()));
        } else {
            messages.info(new DefaultBundleKey("booking_doesNotExist")).defaults(
                    "Our records indicate that the booking you selected has already been canceled.");
        }

        bookingsForUser.remove(selectedBooking);
    }

    private void fetchBookingsForCurrentUser() {
        String username = currentUserInstance.get().getUsername();
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Booking> cquery = builder.createQuery(Booking.class);
        Root<Booking> booking = cquery.from(Booking.class);
        booking.fetch(Booking_.hotel, JoinType.INNER);
        cquery.select(booking).where(builder.equal(booking.get(Booking_.user).get(User_.username), username))
                .orderBy(builder.asc(booking.get(Booking_.checkinDate)));

        bookingsForUser = entityManager.createQuery(cquery).getResultList();
    }

}
