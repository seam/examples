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
package org.jboss.seam.examples.booking.session;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Named;
import javax.context.SessionScoped;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.inject.Current;
import javax.inject.Produces;
import javax.inject.manager.Manager;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.model.Hotel;

public
@Named("hotelSearch")
@Stateful
@SessionScoped
class HotelSearchBean implements HotelSearch
{
   @Current Manager manager;
   @Current SearchCriteria criteria;

   private @PersistenceContext EntityManager em;
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

   public
   @Produces
   @Named
   //@RequestScoped // if enabled, variable doesn't get updated after the action is executed w/o a redirect
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

   @Remove
   public void destroy()
   {
   }

   private void queryHotels(SearchCriteria criteria)
   {
      List<Hotel> results = em.createQuery(
         "select h from Hotel h where lower(h.name) like :pattern or lower(h.city) like :pattern or lower(h.zip) like :pattern or lower(h.address) like :pattern").
         setParameter("pattern", criteria.getSearchPattern()).setMaxResults(criteria.getPageSize() + 1).setFirstResult(criteria.getPage() * criteria.getPageSize()).
         getResultList();

      System.out.println("Found " + results.size() + " hotels");
      nextPageAvailable = results.size() > criteria.getPageSize();
      if (nextPageAvailable)
      {
         hotels = new ArrayList<Hotel>(results.subList(0, criteria.getPageSize()));
      }
      else
      {
         hotels = results;
      }
   }
}
