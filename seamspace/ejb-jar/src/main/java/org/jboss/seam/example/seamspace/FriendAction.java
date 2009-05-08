package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.FriendComment;
import org.jboss.seam.example.seamspace.model.Member;
import org.jboss.seam.example.seamspace.model.MemberFriend;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Identity;

@Named
@ConversationScoped
public class FriendAction implements Serializable
{
   private static final long serialVersionUID = 4565339001481077911L;

   // @RequestParameter("name")
   private String name;
     
   /*@Current*/ Member authenticatedMember;
      
   @PersistenceContext EntityManager entityManager;
   @Current Identity identity;
   @Current StatusMessages messages;
   @Current Conversation conversation;
   
   FriendComment friendComment;
   MemberFriend friendRequest;
      
   public @Produces @Named FriendComment createComment()
   {      
      try
      {
         conversation.begin();
         Member member = (Member) entityManager.createQuery(
         "from Member where memberName = :memberName")
         .setParameter("memberName", name)
         .getSingleResult();
                  
         // Contexts.getMethodContext().set("friends", member.getFriends());
         identity.checkPermission(member, "createFriendComment");

         friendComment = new FriendComment();
         friendComment.setFriend(authenticatedMember);
         friendComment.setMember(member);
         return friendComment;
      }
      catch (NoResultException ex) 
      { 
         messages.add("Member not found.");
         throw ex;
      }
   }
   
   public void saveComment()
   {
      friendComment.setCommentDate(new Date());
      entityManager.persist(friendComment);
      conversation.end();
   }
   
   public void createRequest()
   {
      try
      {
         Member member = (Member) entityManager.createQuery(
         "from Member where memberName = :memberName")
         .setParameter("memberName", name)
         .getSingleResult();
                  
         // Contexts.getMethodContext().set("friends", member.getFriends());
         identity.checkPermission(member, "createFriendRequest");

         friendRequest = new MemberFriend();
         friendRequest.setFriend(authenticatedMember);
         friendRequest.setMember(member);
      }
      catch (NoResultException ex) 
      { 
         messages.add("Member not found.");
      }
   }

   public void saveRequest()
   {
      friendRequest.getMember().getFriends().add(friendRequest);
      entityManager.persist(friendRequest);
      conversation.end();
   }
   
}
