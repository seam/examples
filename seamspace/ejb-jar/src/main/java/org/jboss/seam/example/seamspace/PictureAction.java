package org.jboss.seam.example.seamspace;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.persistence.EntityManager;

@Named
@ConversationScoped
public class PictureAction
{
   private MemberImage memberImage;
   
   @Current Member authenticatedMember;
   
   @Current EntityManager entityManager;
   @Current Conversation conversation;
   
   public void uploadPicture()
   {
      conversation.begin();
      memberImage = new MemberImage();
   }
   
   public void savePicture()
   {
      memberImage.setMember(entityManager.find(Member.class, authenticatedMember.getMemberId()));
      entityManager.persist(memberImage);
      conversation.end();
   }

   public MemberImage getMemberImage()
   {
      return memberImage;
   }
}
