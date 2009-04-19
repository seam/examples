package org.jboss.seam.trinidad;

import static org.jboss.seam.ScopeType.PAGE;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.AbstractMutable;
import org.jboss.seam.framework.EntityIdentifier;

/**
 * EntityIdentifier manager for EntityCollectionModel
 * @author pmuir
 *
 */

@Name("org.jboss.seam.trinidad.entityKeyManager")
@Scope(PAGE)
@BypassInterceptors
@Install(precedence=BUILT_IN)
public class EntityKeyManager extends AbstractMutable
{
   
   private List<EntityIdentifier> rows = new ArrayList<EntityIdentifier>();
 
   public static EntityKeyManager instance()
   {
      return (EntityKeyManager) Component.getInstance(EntityKeyManager.class);
   }

   @Transactional
   public int getIndex(Integer key, List wrappedList, EntityManager entityManager)
   {
      Object entity = rows.get(key).find(entityManager);
      int index = wrappedList.indexOf(entity);
      return index;
   }

   
   @Transactional
   public Object getKey(int rowIndex, List wrappedList, EntityManager entityManager)
   {
      EntityIdentifier key = new EntityIdentifier(wrappedList.get(rowIndex), entityManager);
      if (!rows.contains(key))
      {
         rows.add(key);
         setDirty();
      }
      return rows.indexOf(key);
   }
   
}
