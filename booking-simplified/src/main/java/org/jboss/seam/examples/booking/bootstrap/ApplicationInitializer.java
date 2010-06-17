/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.examples.booking.bootstrap;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.faces.event.PostConstructApplicationEvent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.User;
import org.slf4j.Logger;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Stateless
@Alternative
public class ApplicationInitializer
{
   @PersistenceContext
   private EntityManager em;

   @Inject
   Logger log;

   private final List<User> users = new ArrayList<User>();
   private final List<Hotel> hotels = new ArrayList<Hotel>();

   public ApplicationInitializer()
   {
      users.add(new User("Dan Allen", "dan", "dan@example.com", "laurel"));
      users.add(new User("Pete Muir", "pete", "pete@example.com", "edinburgh"));
      users.add(new User("Lincoln Baxter III", "lincoln", "lincoln@example.com", "charlotte"));
      users.add(new User("Shane Bryzak", "shane", "shane@example.com", "brisbane"));
      users.add(new User("Gavin King", "gavin", "gavin@example.com", "mexico"));

      hotels.add(new Hotel(129, 3, "Marriott Courtyard", "Tower Place, Buckhead", "Atlanta", "GA", "30305", "USA"));
      hotels.add(new Hotel(84, 4, "Doubletree Atlanta-Buckhead", "3342 Peachtree Road NE", "Atlanta", "GA", "30326", "USA"));
      hotels.add(new Hotel(289, 4, "W New York - Union Square", "201 Park Avenue South", "New York", "NY", "10003", "USA"));
      hotels.add(new Hotel(219, 3, "W New York", "541 Lexington Avenue", "New York", "NY", "10022", "USA"));
      hotels.add(new Hotel(250, 3, "Hotel Rouge", "1315 16th Street NW", "Washington", "DC", "20036", "USA"));
      hotels.add(new Hotel(159, 4, "70 Park Avenue Hotel", "70 Park Avenue, 38th St", "New York", "NY", "10016", "USA"));
      hotels.add(new Hotel(198, 4, "Parc 55", "55 Cyril Magnin Street", "San Francisco", "CA", "94102", "USA"));
      hotels.add(new Hotel(189, 4, "Conrad Miami", "1395 Brickell Ave", "Miami", "FL", "33131", "USA"));
      hotels.add(new Hotel(111, 4, "Grand Hyatt", "345 Stockton Street", "San Francisco", "CA", "94108", "USA"));
      hotels.add(new Hotel(54, 1, "Super 8 Eau Claire Campus Area", "1151 W MacArthur Ave", "Eau Claire", "WI", "54701", "USA"));
      hotels.add(new Hotel(199, 4, "San Francisco Marriott", "55 Fourth Street", "San Francisco", "CA", "94103", "USA"));
      hotels.add(new Hotel(543, 4, "Hilton Diagonal Mar", "Passeig del Taulat 262-264", "Barcelona", "Catalunya", "08019", "ES"));
      hotels.add(new Hotel(335, 5, "Hilton Tel Aviv", "Independence Park", "Tel Aviv", null, "63405", "IL"));
      hotels.add(new Hotel(242, 5, "InterContinental Hotel Tokyo Bay", "1-15-2 Kaigan", "Tokyo", "Minato", "105", "JP"));
      hotels.add(new Hotel(130, 4, "Hotel Beaulac", " Esplanade Léopold-Robert 2", "Neuchatel", null, "2000", "CH"));
      hotels.add(new Hotel(266, 5, "Conrad Treasury Place", "130 William Street", "Brisbane", "QL", "4001", "AU"));
      hotels.add(new Hotel(170, 4, "Ritz-Carlton Montreal", "1228 Sherbrooke St West", "Montreal", "Quebec", "H3G1H6", "CA"));
      hotels.add(new Hotel(179, 4, "Ritz-Carlton Atlanta", "181 Peachtree St NE", "Atlanta", "GA", "30303", "USA"));
      hotels.add(new Hotel(145, 4, "Swissotel Sydney", "68 Market Street", "Sydney", "NSW", "2000", "AU"));
      hotels.add(new Hotel(178, 4, "Meliá White House", "Albany Street Regents Park", "London", null, "NW13UP", "GB"));
      hotels.add(new Hotel(159, 3, "Hotel Allegro", "171 W Randolph Street", "Chicago", "IL", "60601", "USA"));
      hotels.add(new Hotel(296, 5, "Caesars Palace", "3570 Las Vegas Blvd S", "Las Vegas", "NV", "89109", "USA"));
      hotels.add(new Hotel(300, 4, "Mandalay Bay Resort & Casino", "3950 Las Vegas Blvd S", "Las Vegas", "NV", "89119", "USA"));
      hotels.add(new Hotel(100, 2, "Hotel Cammerpoorte", "Nationalestraat 38-40", "Antwerp", null, "2000", "BE"));
   }

   public void init(@Observes final PostConstructApplicationEvent event)
   {
      try
      {
         persist(users);
         persist(hotels);
      }
      catch (Exception e)
      {
         log.error("Encountered error seeding the database", e);
      }
   }

   private void persist(final List entities)
   {
      for (Object e : entities)
      {
         persist(e);
      }
   }

   private void persist(final Object entity)
   {
      try
      {
         em.persist(entity);
      }
      catch (ConstraintViolationException e)
      {
         for (ConstraintViolation v : e.getConstraintViolations())
         {
            log.error("Cannot persist entity because it has validation errors " + v.getRootBean() + ": " + v.getPropertyPath() + " " + v.getMessage());
         }
      }
   }
}
