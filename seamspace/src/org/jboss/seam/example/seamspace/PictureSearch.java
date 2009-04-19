package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Delete;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.security.Identity;

@Name("pictureSearch")
@Scope(EVENT)
public class PictureSearch implements Serializable
{
   private static final long serialVersionUID = -1868188969326866331L;
   
   private String memberName;
   
   @In
   private EntityManager entityManager;
   
   @Out(required = false)
   private List<MemberImage> memberImages;
   
   @RequestParameter
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
      Identity.instance().filterByPermission(memberImages, "view");
   }
}
