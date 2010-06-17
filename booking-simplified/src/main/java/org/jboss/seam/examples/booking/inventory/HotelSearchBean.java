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
package org.jboss.seam.examples.booking.inventory;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.Hotel_;
import org.jboss.seam.international.status.MessageFactory;
import org.slf4j.Logger;

@Named("hotelSearch")
@Stateful
@SessionScoped
public class HotelSearchBean implements HotelSearch
{
   @Inject
   private Logger log;

   @PersistenceContext
   private EntityManager em;

   @Inject
   private SearchCriteria criteria;

   @Inject
   private MessageFactory msg;

   private boolean nextPageAvailable = false;

   private List<Hotel> hotels = new ArrayList<Hotel>();

   public void find()
   {
      criteria.firstPage();
      queryHotels(criteria);
   }

   public void nextPage()
   {
      criteria.nextPage();
      queryHotels(criteria);
   }

   public void previousPage()
   {
      criteria.previousPage();
      queryHotels(criteria);
   }

   public @Produces
   @Named
   @Dependent
   // @RequestScoped // if enabled, variable doesn't get updated after the
   // action is executed w/o a redirect
   List<Hotel> getHotels()
   {
      return hotels;
   }

   public boolean isNextPageAvailable()
   {
      return nextPageAvailable;
   }

   public boolean isPreviousPageAvailable()
   {
      return criteria.getPage() > 0;
   }

   private void queryHotels(final SearchCriteria criteria)
   {
      CriteriaBuilder builder = em.getCriteriaBuilder();
      CriteriaQuery<Hotel> cquery = builder.createQuery(Hotel.class);
      Root<Hotel> hotel = cquery.from(Hotel.class);
      // QUESTION can like create the pattern for us?
      cquery.select(hotel)
            .where(builder.or(
                  builder.like(builder.lower(hotel.get(Hotel_.name)), criteria.getSearchPattern()),
                  builder.like(builder.lower(hotel.get(Hotel_.city)), criteria.getSearchPattern()),
                  builder.like(builder.lower(hotel.get(Hotel_.zip)), criteria.getSearchPattern()),
                  builder.like(builder.lower(hotel.get(Hotel_.address)), criteria.getSearchPattern())));

      List<Hotel> results = em.createQuery(cquery)
            .setMaxResults(criteria.getFetchSize())
            .setFirstResult(criteria.getFetchOffset())
            .getResultList();

      nextPageAvailable = results.size() > criteria.getPageSize();
      if (nextPageAvailable)
      {
         // NOTE create new ArrayList since subList creates unserializable list
         hotels = new ArrayList<Hotel>(results.subList(0, criteria.getPageSize()));
      }
      else
      {
         hotels = results;
      }
      log.info(msg.info("Found {0} hotel(s) matching search term [ {1} ] (limit {2})")
            .textParams(hotels.size(), criteria.getQuery(), criteria.getPageSize()).build().getText());
   }
}
