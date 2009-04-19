//$Id: HotelSearching.java,v 1.1 2007/06/23 18:33:59 pmuir Exp $
package org.jboss.seam.example.booking;

import javax.ejb.Local;

@Local
public interface HotelSearching
{
   public int getPageSize();
   public void setPageSize(int pageSize);
   
   public String getSearchString();
   public void setSearchString(String searchString);
   
   public void find();
   public void nextPage();
   public boolean isNextPageAvailable();

   public String getSearchPattern();

   public void destroy();
   
}
