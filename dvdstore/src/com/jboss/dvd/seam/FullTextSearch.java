//$Id$
package com.jboss.dvd.seam;

/**
 * @author Emmanuel Bernard
 */
public interface FullTextSearch
{
   public String getSearchQuery();
   public void setSearchQuery(String searchQuery);

   public Long getSelectedId();
   public void setSelectedId(Long id);


   public int getNumberOfResults();
   public boolean isLastPage();
   public boolean isFirstPage();
   public void nextPage();
   public void prevPage();

   public String doSearch();
   public void selectFromRequest();
   public void addToCart();
   public void addAllToCart();

   public int getPageSize();
   public void setPageSize(int pageSize);
  
   public void reset();
   public void destroy();
}
