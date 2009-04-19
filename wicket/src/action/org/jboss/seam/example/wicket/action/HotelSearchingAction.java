//$Id: HotelSearchingAction.java,v 1.20 2007/06/27 00:06:49 gavin Exp $
package org.jboss.seam.example.wicket.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;

@Stateful
@Name("hotelSearch")
@Scope(ScopeType.SESSION)
@Restrict("#{identity.loggedIn}")
public class HotelSearchingAction implements HotelSearching
{
   
   @PersistenceContext
   private EntityManager em;
   
   private String searchString;
   
   private List<Hotel> hotels = new ArrayList<Hotel>();
   
   public void find()
   {
      queryHotels();
   }
      
   private void queryHotels()
   {
      hotels = em.createQuery("select h from Hotel h where lower(h.name) like #{pattern} or lower(h.city) like #{pattern} or lower(h.zip) like #{pattern} or lower(h.address) like #{pattern}")
            .getResultList();
   }
   
   @Factory(value="pattern", scope=ScopeType.EVENT)
   public String getSearchPattern()
   {
      return searchString==null ? 
            "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
   }
   
   public String getSearchString()
   {
      return searchString;
   }
   
   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }
   
   public List<Hotel> getHotels()
   {
      return hotels;
   }
   
   @Remove
   public void destroy() {}
}