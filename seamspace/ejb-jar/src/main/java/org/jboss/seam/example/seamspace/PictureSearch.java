package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Named;
import javax.context.RequestScoped;
import javax.inject.Current;
import javax.persistence.EntityManager;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.Secure;
import org.jboss.seam.security.annotations.Delete;

@Named
@RequestScoped
@Secure
public class PictureSearch implements Serializable
{
   private static final long serialVersionUID = -1868188969326866331L;
   
   private String memberName;
   
   @Current EntityManager entityManager;
   @Current Identity identity;
   
   private List<MemberImage> memberImages;
   
   //@RequestParameter
   private Integer imageId;
   
   public String getMemberName()
   {
      return memberName;
   }

   public void setMemberName(String memberName)
   {
      this.memberName = memberName;
   }
   
   public void delete(@Delete MemberImage image)
   {
      entityManager.remove(image);
   }
   
   public MemberImage lookupImage()
   {
      return entityManager.find(MemberImage.class, imageId);
   }
   
   @SuppressWarnings("unchecked")
   public void loadMemberPictures()
   {
      memberImages = (List<MemberImage>) entityManager.createQuery(
            "select i from MemberImage i where i.member.memberName = :name and not i = i.member.picture")
            .setParameter("name", memberName)
            .getResultList();      
      identity.filterByPermission(memberImages, "view");
   }
}
