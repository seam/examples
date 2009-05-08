package org.jboss.seam.example.seamspace;

import java.io.Serializable;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.Member;
import org.jboss.seam.example.seamspace.model.MemberImage;

@Named
@ConversationScoped
public class PictureAction implements Serializable
{
   private static final long serialVersionUID = -5253276146026546823L;

   private MemberImage memberImage;
   
   /*@Current*/  Member authenticatedMember;
   
   @PersistenceContext EntityManager entityManager;
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
