package org.jboss.seam.trinidad;

import org.jboss.seam.framework.HibernateEntityQuery;
import org.jboss.seam.framework.Query;

public class HibernateEntityCollectionModel extends SeamCollectionModel
{
   
   private HibernateEntityQuery hibernateEntityQuery;

   public HibernateEntityCollectionModel(HibernateEntityQuery query)
   {
      this.hibernateEntityQuery = query;
   }

   @Override
   protected Query getQuery()
   {
      return hibernateEntityQuery;
   }

   @Override
   public Object getRowKey()
   {   
      if (getRowIndex() == -1)
      {
        return null;
      }
      else
      {
        return HibernateEntityKeyManager.instance().getKey(getRowIndex() - getFirstResult(), getWrappedList(), hibernateEntityQuery.getSession());
      }
   }

   @Override
   public void setRowKey(Object rowKey)
   {
      if (rowKey == null)
      {
         setRowIndex(-1);
      }
      else
      {
         setRowIndex(HibernateEntityKeyManager.instance().getIndex((Integer) rowKey, getWrappedList(), hibernateEntityQuery.getSession()) + getFirstResult());
      }
   }

}
