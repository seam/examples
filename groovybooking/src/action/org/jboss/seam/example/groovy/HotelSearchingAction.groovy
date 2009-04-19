//$Id$
package org.jboss.seam.example.groovy

import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

import org.jboss.seam.ScopeType
import org.jboss.seam.annotations.Destroy
import org.jboss.seam.annotations.Name
import org.jboss.seam.annotations.Scope
import org.jboss.seam.annotations.datamodel.DataModel

import org.jboss.seam.annotations.In

import org.jboss.seam.annotations.Factory

@Name("hotelSearch")
@Scope(ScopeType.SESSION)
class HotelSearchingAction
{

   @In
   private EntityManager em

   String searchString
   int pageSize = 10
   int page

   @DataModel
   List<Hotel> hotels

   void find()
   {
      page = 0
      queryHotels()
   }

   void nextPage()
   {
      page++
      queryHotels()
   }

   private void queryHotels()
   {
      def query = em.createQuery('''
         select h from Hotel h where
         lower(h.name) like #{pattern}
         or lower(h.city) like #{pattern}
         or lower(h.zip) like #{pattern}
         or lower(h.address) like #{pattern}
         ''')
      query.maxResults = pageSize
      query.firstResult = page * pageSize
      hotels = query.getResultList()	
   }

   @Factory(value="pattern", scope=ScopeType.EVENT)
   String getSearchPattern()
   {
      return searchString ?
            "%${searchString.toLowerCase().replace('*', '%')}%" :
            "%";
   }

   boolean isNextPageAvailable()
   {
      return hotels && hotels.size()==pageSize
   }
}
