package org.jboss.seam.examples.booking.action;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Named;
import javax.context.RequestScoped;
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
   @RequestScoped
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
