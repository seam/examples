package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.STATELESS;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

@Scope(STATELESS)
@Name("contentAction")
public class ContentAction
{
   @In EntityManager entityManager;   
   
   public MemberImage getImage(int imageId)
   {
      MemberImage img = entityManager.find(MemberImage.class, imageId);
      
      if (img == null || !Identity.instance().hasPermission(img, "view"))
         return null;
      else
         return img;
   }
}
