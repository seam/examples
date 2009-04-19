package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

@Name("auctionSearch")
@Scope(ScopeType.SESSION)
public class AuctionSearchAction implements Serializable
{
   private static final long serialVersionUID = -3548004575336733926L;

   @In
   EntityManager entityManager;
   
   private int pageSize = 10;
   private int page = 0;
   
   private String searchTerm;
   private Category searchCategory;
   private boolean titleAndDescription;
   
   @DataModel
   private List<Auction> auctions;
   
   @DataModelSelection
   private Auction selectedAuction;
   
   private Map<Category,Long> searchCategories = new HashMap<Category,Long>();

   @SuppressWarnings("unchecked")
   public void queryAuctions()
   {
      StringBuilder qry = new StringBuilder();
      
      qry.append("from Auction a where a.status = 1 and endDate >= #{currentDatetime}");
      
      if (titleAndDescription)
      {
         qry.append(" and (lower(title) like #{searchPattern} or lower(description) like #{searchPattern})");
      }
      else
      {
         qry.append(" and lower(title) like #{searchPattern}");
      }
      
      if (searchCategory != null)
      {
         qry.append(" and a.category in (#{subCategories})");
      }
            
      auctions = entityManager.createQuery(qry.toString())
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .getResultList();      
      
      searchCategories.clear();
      
      StringBuilder catQuery = new StringBuilder();
      catQuery.append("select a.category.categoryId, count(a) from Auction a " +
            "where lower(a.title) like #{searchPattern} " +
            "and a.endDate >= #{currentDatetime} and a.status = 1 ");
      
      if (searchCategory != null)
      {
         catQuery.append("and a.category in (#{subCategories}) ");
      }
             
      catQuery.append("group by a.category.categoryId");
      
      
      for (Object[] result : (List<Object[]>) entityManager.createQuery(
           catQuery.toString()).getResultList())
      {
         searchCategories.put(entityManager.find(Category.class, result[0]), (Long) result[1]);
      }
   }
   
   public void queryAllAuctions()
   {
      searchCategory = null;
      queryAuctions();
   }
   
   @Factory(value="searchPattern", scope=ScopeType.EVENT)
   public String getSearchPattern()
   {
      return searchTerm == null ? 
            "%" : '%' + searchTerm.toLowerCase().replace('*', '%') + '%';
   }   
   
   public String getSearchTerm()
   {
      return searchTerm;
   }
   
   public void setSearchTerm(String searchTerm)
   {
      this.searchTerm = searchTerm;
   }
   
   public int getPageSize()
   {
      return pageSize;
   }
   
   public void setPageSize(int pageSize)
   {
      this.pageSize = pageSize;
   }
   
   public int getPage()
   {
      return page;
   }
   
   public void setPage(int page)
   {
      this.page = page;
   }
   
   public List<Entry> getSearchCategories()
   {
      List<Entry> cats = new ArrayList<Entry>(searchCategories.entrySet());
      Collections.sort(cats, new Comparator<Entry>() {
        public int compare(Entry e1, Entry e2) {
           return ((Category) e1.getKey()).getName().compareToIgnoreCase(
                 ((Category) e2.getKey()).getName());
        }
      });
      return cats;
   }   
   
   public void selectCategory(Category category)
   {
      setSearchCategory(category);
      queryAuctions();
   }
   
   @Factory(value="subCategories", scope=ScopeType.EVENT)
   public Set<Category> getSubCategories()
   {
      Set<Category> categories = new HashSet<Category>();
      categories.add(searchCategory);      
      addSubCategories(searchCategory, categories);      
      return categories;
   }

   @SuppressWarnings("unchecked")
   private void addSubCategories(Category parent, Set<Category> categories)
   {
      List<Category> children = entityManager.createQuery(
            "from Category where parent = :parent")
            .setParameter("parent", parent)
            .getResultList();
      
      for (Category child : children)
      {
         categories.add(child);
         addSubCategories(child, categories);
      }      
   }
   
   public Category getSearchCategory()
   {
      return searchCategory;
   }
   
   public List<Auction> getAuctions()
   {
      return auctions;
   }
   
   public void setSearchCategory(Category category)
   {
      this.searchCategory = category;  
   }
   
   public Integer getSelectedCategoryId()
   {
      return searchCategory != null ? searchCategory.getCategoryId() : null;
   }
   
   public void setSelectedCategoryId(Integer categoryId)
   {
      this.searchCategory = entityManager.find(Category.class, categoryId);
   }
   
   public boolean isTitleAndDescription()
   {
      return titleAndDescription;
   }
   
   public void setTitleAndDescription(boolean value)
   {
      this.titleAndDescription = value;
   }
}
