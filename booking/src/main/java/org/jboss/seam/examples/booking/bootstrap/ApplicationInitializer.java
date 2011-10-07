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
package org.jboss.seam.examples.booking.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.validation.ConstraintViolationException;

import org.jboss.solder.logging.Logger;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.Initialized;

/**
 * An alternative bean used to import seed data into the database when the application is being initialized.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
// @Stateless // can't use EJB since they are not yet available for lookup when initialized event is fired
@Alternative
public class ApplicationInitializer {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private UserTransaction utx;

    @Inject
    private Logger log;

    private final List<User> users = new ArrayList<User>();
    private final List<Hotel> hotels = new ArrayList<Hotel>();

    public ApplicationInitializer() {
        users.addAll(Arrays.asList(
                new User("Shane Bryzak", "shane", "shane@example.com", "brisbane"),
                new User("Dan Allen", "dan", "dan@example.com", "laurel"),
                new User("Lincoln Baxter III", "lincoln", "lincoln@example.com", "charlotte"),
                new User("Jose Freitas", "jose", "jose.freitas@example.com", "brazil")));

        hotels.addAll(Arrays.asList(
                new Hotel(129, 3, "Marriott Courtyard", "Tower Place, Buckhead", "Atlanta", "GA", "30305", "USA"),
                new Hotel(84, 4, "Doubletree Atlanta-Buckhead", "3342 Peachtree Road NE", "Atlanta", "GA", "30326", "USA"),
                new Hotel(289, 4, "W New York - Union Square", "201 Park Avenue South", "New York", "NY", "10003", "USA"),
                new Hotel(219, 3, "W New York", "541 Lexington Avenue", "New York", "NY", "10022", "USA"),
                new Hotel(250, 3, "Hotel Rouge", "1315 16th Street NW", "Washington", "DC", "20036", "USA"),
                new Hotel(159, 4, "70 Park Avenue Hotel", "70 Park Avenue, 38th St", "New York", "NY", "10016", "USA"),
                new Hotel(198, 4, "Parc 55", "55 Cyril Magnin Street", "San Francisco", "CA", "94102", "USA"),
                new Hotel(189, 4, "Conrad Miami", "1395 Brickell Ave", "Miami", "FL", "33131", "USA"),
                new Hotel(111, 4, "Grand Hyatt", "345 Stockton Street", "San Francisco", "CA", "94108", "USA"),
                new Hotel(54, 1, "Super 8 Eau Claire Campus Area", "1151 W MacArthur Ave", "Eau Claire", "WI", "54701", "USA"),
                new Hotel(199, 4, "San Francisco Marriott", "55 Fourth Street", "San Francisco", "CA", "94103", "USA"),
                new Hotel(543, 4, "Hilton Diagonal Mar", "Passeig del Taulat 262-264", "Barcelona", "Catalunya", "08019", "ES"),
                new Hotel(335, 5, "Hilton Tel Aviv", "Independence Park", "Tel Aviv", null, "63405", "IL"),
                new Hotel(242, 5, "InterContinental Hotel Tokyo Bay", "1-15-2 Kaigan", "Tokyo", "Minato", "105", "JP"),
                new Hotel(130, 4, "Hotel Beaulac", "Esplanade Léopold-Robert 2", "Neuchatel", null, "2000", "CH"),
                new Hotel(266, 5, "Conrad Treasury Place", "130 William Street", "Brisbane", "QL", "4001", "AU"),
                new Hotel(170, 4, "Ritz-Carlton Montreal", "1228 Sherbrooke St West", "Montreal", "Quebec", "H3G1H6", "CA"),
                new Hotel(179, 4, "Ritz-Carlton Atlanta", "181 Peachtree St NE", "Atlanta", "GA", "30303", "USA"),
                new Hotel(145, 4, "Swissotel Sydney", "68 Market Street", "Sydney", "NSW", "2000", "AU"),
                new Hotel(178, 4, "Meliá White House", "Albany Street Regents Park", "London", null, "NW13UP", "GB"),
                new Hotel(159, 3, "Hotel Allegro", "171 W Randolph Street", "Chicago", "IL", "60601", "USA"),
                new Hotel(296, 5, "Caesars Palace", "3570 Las Vegas Blvd S", "Las Vegas", "NV", "89109", "USA"),
                new Hotel(300, 4, "Mandalay Bay Resort & Casino", "3950 Las Vegas Blvd S", "Las Vegas", "NV", "89119", "USA"),
                new Hotel(100, 2, "Hotel Cammerpoorte", "Nationalestraat 38-40", "Antwerp", null, "2000", "BE")));
    }

    /**
     * Import seed data when Seam Servlet fires an event notifying observers that the web application is being initialized.
     */
    public void importData(@Observes @Initialized WebApplication webapp) {
        log.info("Importing seed data for application " + webapp.getName());
        // use manual transaction control since this is a managed bean
        try {
            utx.begin();
            // AS7-2045
            entityManager.createQuery("delete from Booking").executeUpdate();
            entityManager.createQuery("delete from Hotel").executeUpdate();
            entityManager.createQuery("delete from User").executeUpdate();
            
            persist(users);
            persist(hotels);
            utx.commit();
            log.info("Seed data successfully imported");
        } catch (Exception e) {
            log.error("Import failed. Seed data will not be available.", e);
            try {
                if (utx.getStatus() == Status.STATUS_ACTIVE) {
                    try {
                        utx.rollback();
                    } catch (Exception rbe) {
                        log.error("Error rolling back transaction", rbe);
                    }
                }
            } catch (Exception se) {
            }
        }
    }

    private void persist(List<?> entities) {
        for (Object e : entities) {
            persist(e);
        }
    }

    private void persist(Object entity) {
        // use a try-catch block here so we can capture identity
        // of entity that fails to persist
        try {
            entityManager.persist(entity);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new PersistenceException("Cannot persist invalid entity: " + entity);
        } catch (PersistenceException e) {
            throw new PersistenceException("Error persisting entity: " + entity, e);
        }
    }
}
