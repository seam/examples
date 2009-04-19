//$Id$
package org.jboss.seam.example.booking;
import java.util.List;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
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
      hotels = em.createQuery("select h from Hotel h where lower(h.name) like #{pattern} or lower(h.city) like #{pattern} or lower(h.zip) like #{pattern} or lower(h.address) like #{pattern}")
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .getResultList();
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
   
   @Destroy @Remove
   public void destroy() {}
}