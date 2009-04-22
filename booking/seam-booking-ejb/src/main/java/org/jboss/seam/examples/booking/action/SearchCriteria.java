package org.jboss.seam.examples.booking.action;

import java.io.Serializable;
import javax.annotation.Named;
import javax.context.SessionScoped;

public
@Named
@SessionScoped
class SearchCriteria implements Serializable
{
   private String searchString = "";
   private int pageSize = 5;
   private int page = 0;

   public String getSearchPattern() {
      return searchString == null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
   }

   public int getPage()
   {
      return page;
   }

   public void setPage(int page)
   {
      this.page = page;
   }

   public int getPageSize()
   {
      return pageSize;
   }

   public void setPageSize(int pageSize)
   {
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

   public void nextPage()
   {
      page++;
   }

   public void previousPage()
   {
      if (page > 0) {
         page--;
      }
   }

   public void firstPage()
   {
      page = 0;
   }
}
