package org.jboss.seam.example.seamspace;

import java.io.Serializable;

import javax.inject.Current;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.MemberImage;
import org.jboss.seam.security.Identity;

public class ContentAction implements Serializable
{
   private static final long serialVersionUID = -3028986030113894868L;
   
   @PersistenceContext EntityManager entityManager;
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
