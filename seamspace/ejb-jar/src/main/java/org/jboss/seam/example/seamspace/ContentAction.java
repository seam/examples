package org.jboss.seam.example.seamspace;

import javax.annotation.Named;
import javax.inject.Current;
import javax.persistence.EntityManager;

import org.jboss.seam.security.Identity;

@Named
public class ContentAction
{
   @Current EntityManager entityManager;
   @Current Identity identity;
   
   public MemberImage getImage(int imageId)
   {
      MemberImage img = entityManager.find(MemberImage.class, imageId);
      
      if (img == null || !identity.hasPermission(img, "view"))
         return null;
      else
         return img;
   }
}
