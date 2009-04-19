package org.jboss.seam.example.seamdiscs.action;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.example.seamdiscs.model.BandMember;
import org.jboss.seam.framework.EntityController;

@Name("bandMemberFinder")
public class BandMemberFinder extends EntityController
{

   @Transactional
   public List<BandMember> getBandMembers(Object name)
   {
      return getEntityManager().createQuery("select bm from BandMember bm where lower(bm.name) like lower(:name + '%')").setParameter("name", name).getResultList();
   }
   
}
