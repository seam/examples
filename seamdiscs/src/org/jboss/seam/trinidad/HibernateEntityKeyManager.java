package org.jboss.seam.trinidad;

import static org.jboss.seam.ScopeType.PAGE;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.AbstractMutable;
import org.jboss.seam.framework.HibernateEntityIdentifier;

/**
 * EntityIdentifier manager for EntityCollectionModel
 * @author pmuir
 *
 */

@Name("org.jboss.seam.trinidad.hibernateEntityKeyManager")
@Scope(PAGE)
@BypassInterceptors
@Install(precedence=BUILT_IN)
public class HibernateEntityKeyManager extends AbstractMutable
{
   
   private List<HibernateEntityIdentifier> rows = new ArrayList<HibernateEntityIdentifier>();
 
   public static HibernateEntityKeyManager instance()
   {
      return (HibernateEntityKeyManager) Component.getInstance(HibernateEntityKeyManager.class);
   }

   @Transactional
   public int getIndex(Integer key, List wrappedList, Session session)
   {
      Object entity = rows.get(key).find(session);
      int index = wrappedList.indexOf(entity);
      return index;
   }

   
   @Transactional
   public Object getKey(int rowIndex, List wrappedList, Session session)
   {
      HibernateEntityIdentifier key = new HibernateEntityIdentifier(wrappedList.get(rowIndex), session);
      if (!rows.contains(key))
      {
         rows.add(key);
         setDirty();
      }
      return rows.indexOf(key);
   }
   
}
