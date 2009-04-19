package org.jboss.seam.trinidad;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortCriterion;
import org.jboss.seam.framework.Query;
import org.jboss.seam.util.Strings;

/**
 * Abstract base class for an Apache Trinidad CollectionModel
 * 
 * Implementing classes need to provide conversion between the
 * current row index and a key for the unchanging row.
 * 
 * Using rowIndex when backing the CollectionModel with a Query
 * is not possible as sorting and paging alters the rowIndex
 * outside the control of the CollectionModel.
 * 
 * @author pmuir
 *
 */
public abstract class SeamCollectionModel extends CollectionModel
{

   private static final Pattern COMMA = Pattern.compile(",");
   private static final Pattern SPACE = Pattern.compile("\\s+");

   //private Object rowKey;
   
   private int rowIndex = -1;
   
   private List<SortCriterion> criteria;
   
   @Override
   public Object getWrappedData()
   {
      return getWrappedList();
   }

   public void refresh()
   {
      getQuery().refresh();
   }

   @Override
   public int getRowCount()
   {
      return getQuery().getResultCount().intValue();
   }

   @Override
   public void setWrappedData(Object arg0)
   {
      throw new UnsupportedOperationException("Immutable DataModel");
   }

   protected List getWrappedList()
   {
      return getQuery().getResultList();
   }
   
   protected abstract Query getQuery();
   
   @Override
   public boolean isSortable(String property)
   {
      return true;
   }

   @Override
   public List<SortCriterion> getSortCriteria()
   {
      if (criteria == null)
      {
         criteria = asCriteria(getQuery().getOrder());
      }
      return criteria;
   }

   @Override
   public void setSortCriteria(List<SortCriterion> criteria)
   {
      if (criteria != null && !criteria.equals(getSortCriteria()))
      {
         getQuery().setOrder(asQl(criteria));
         this.criteria = criteria;
      }
   }
   
   @Override
   public void setRowIndex(int rowIndex)
   {
      this.rowIndex = rowIndex;
      //rowKey = null;
   }

   @Override
   public int getRowIndex()
   {
      return rowIndex;
   }

   @Override
   public Object getRowData()
   {
      // We can attempt to do lazy loading
      if (getQuery().getMaxResults() != null)
      {
         boolean refresh = false;
         // Lazy load data
         refresh = page();
         if (refresh)
         {
            refresh();
         }
         return getWrappedList().get(getRowIndex() - getFirstResult());
      }
      else
      {
         return getWrappedList().get(getRowIndex());
      }
   }

   private boolean page()
   {
      if (getRowIndex() < getFirstResult())
      {
         while (getRowIndex() < getFirstResult())
         {
            getQuery().previous();
         }
         return true;
      }
      else if (getRowIndex() >= getQuery().getNextFirstResult())
      {
         while (getRowIndex() >= getQuery().getNextFirstResult())
         {
            getQuery().next();
         }
         return true;
      }
      else
      {
         return false;
      }
   }

   protected int getFirstResult()
   {
      if (getQuery().getFirstResult() == null)
      {
         getQuery().setFirstResult(0);
      }
      return getQuery().getFirstResult();
   }

   @Override
   public boolean isRowAvailable()
   {
      return getRowIndex() >= 0 && getRowIndex() < getRowCount();
   }
   
   public static String asQl(List<SortCriterion> criteria)
   {
      if (criteria != null && criteria.size() > 0)
      {
         StringBuffer sb = new StringBuffer();
         boolean first = true;
         for (SortCriterion sortCriterion : criteria)
         {
            if (first) 
            {
               first = false;
            }
             else 
             {
               sb.append(',');
            }
            sb.append(sortCriterion.getProperty()).append(sortCriterion.isAscending() ? " ASC" : " DESC");
         }
         return sb.toString();
      }
      return null;
   }
   
   public static List<SortCriterion> asCriteria(String sql) 
   {
      if (!Strings.isEmpty(sql))
      {
         String[] tokens = COMMA.split(sql.trim());
         List<SortCriterion> criteria = new ArrayList<SortCriterion>(tokens.length);
         for (int i = 0; i != tokens.length; i++) 
         {
            String[] terms = SPACE.split(tokens[i].trim());
            SortCriterion sortCriterion = new SortCriterion(terms[0], terms.length == 1 ? false : "ASC".equalsIgnoreCase(terms[1]));
            criteria.add(i, sortCriterion);
         }
         return criteria;
      }
      return new ArrayList<SortCriterion>();
   }
   
}