//$Id$
package org.jboss.seam.example.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

@Name("hotelSearch")
@Scope(ScopeType.SESSION)
public class HotelSearchingAction implements Serializable
{
   
   @In
   private Session bookingDatabase;
   
   private String searchString;
   private int pageSize = 10;
   private int page;
   
   @DataModel
   private List<Hotel> hotels;
   
   public void find()
   {
      page = 0;
      queryHotels();
   }

   public void nextPage()
   {
      page++;
      queryHotels();
   }
      
   private void queryHotels()
   {
      hotels = bookingDatabase.createQuery("select h from Hotel h where lower(h.name) like #{pattern} or lower(h.city) like #{pattern} or lower(h.zip) like #{pattern} or lower(h.address) like #{pattern}")
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .list();
   }

   @Factory(value="pattern", scope=ScopeType.EVENT)
   public String getSearchPattern()
   {
      return searchString==null ?
            "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
   }
   
   public boolean isNextPageAvailable()
   {
      return hotels!=null && hotels.size()==pageSize;
   }
   
   public int getPageSize() {
      return pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
   }

   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }
   
}
