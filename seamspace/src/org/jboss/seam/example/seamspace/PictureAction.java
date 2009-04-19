package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;

@Scope(CONVERSATION)
@Name("pictureAction")
public class PictureAction
{
   private MemberImage memberImage;
   
   @In(required = false)
   private Member authenticatedMember;
   
   @In EntityManager entityManager;
   
   @Begin
   public void uploadPicture()
   {
      memberImage = new MemberImage();
   }
   
   public void savePicture()
   {
      memberImage.setMember(entityManager.find(Member.class, authenticatedMember.getMemberId()));
      entityManager.persist(memberImage);
      Conversation.instance().end();
   }

   public MemberImage getMemberImage()
   {
      return memberImage;
   }
}
