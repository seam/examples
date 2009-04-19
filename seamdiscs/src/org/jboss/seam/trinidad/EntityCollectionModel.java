package org.jboss.seam.trinidad;

import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.framework.Query;

public class EntityCollectionModel extends SeamCollectionModel
{
   private EntityQuery entityQuery;

   public EntityCollectionModel(EntityQuery entityQuery)
   {
      this.entityQuery = entityQuery;
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
        return EntityKeyManager.instance().getKey(getRowIndex() - getFirstResult(), getWrappedList(), entityQuery.getEntityManager());
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
         setRowIndex(EntityKeyManager.instance().getIndex((Integer) rowKey, getWrappedList(), entityQuery.getEntityManager()) + getFirstResult());
      }
   }

   @Override
   protected Query getQuery()
   {
      return entityQuery;
   }

}