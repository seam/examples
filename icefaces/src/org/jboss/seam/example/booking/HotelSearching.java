//$Id: HotelSearching.java,v 1.12 2007/06/27 00:06:49 gavin Exp $
package org.jboss.seam.example.booking;

import java.util.List;

import javax.ejb.Local;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

@Local
public interface HotelSearching
{
   public int getPageSize();
   public void setPageSize(int pageSize);
   
   public String getSearchString();
   public void setSearchString(String searchString);
   public void handleSearchStringChange(ValueChangeEvent e);
   public void handlePageSizeChange(ValueChangeEvent e);
   public List<SelectItem> getCities();
   
   public void find();
   public void nextPage();
   public boolean isNextPageAvailable();

   public void destroy();
   
}