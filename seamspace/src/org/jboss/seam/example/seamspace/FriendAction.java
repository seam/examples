package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Remove;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;

@Scope(CONVERSATION)
@Name("friendAction")
public class FriendAction implements Serializable
{
   private static final long serialVersionUID = 4565339001481077911L;

   @RequestParameter("name")
   private String name;
   
   @Out(required = false)
   private FriendComment friendComment;
   
   @Out(required = false)
   private MemberFriend friendRequest;
   
   @In(required = false)
   private Member authenticatedMember;
      
   @In
   private EntityManager entityManager;
      
   @Factory("friendComment") @Begin
   public void createComment()
   {      
      try
      {
         Member member = (Member) entityManager.createQuery(
         "from Member where memberName = :memberName")
         .setParameter("memberName", name)
         .getSingleResult();
                  
         Contexts.getMethodContext().set("friends", member.getFriends());
         Identity.instance().checkPermission(member, "createFriendComment");

         friendComment = new FriendComment();
         friendComment.setFriend(authenticatedMember);
         friendComment.setMember(member);
      }
      catch (NoResultException ex) 
      { 
         FacesMessages.instance().add("Member not found.");
      }
   }
   
   @End
   public void saveComment()
   {
      friendComment.setCommentDate(new Date());
      entityManager.persist(friendComment);
   }
   
   @Factory("friendRequest") @Begin
   public void createRequest()
   {
      try
      {
         Member member = (Member) entityManager.createQuery(
         "from Member where memberName = :memberName")
         .setParameter("memberName", name)
         .getSingleResult();
                  
         Contexts.getMethodContext().set("friends", member.getFriends());
         Identity.instance().checkPermission(member, "createFriendRequest");

         friendRequest = new MemberFriend();
         friendRequest.setFriend(authenticatedMember);
         friendRequest.setMember(member);
      }
      catch (NoResultException ex) 
      { 
         FacesMessages.instance().add("Member not found.");
      }
   }

   @End
   public void saveRequest()
   {
      friendRequest.getMember().getFriends().add(friendRequest);
      entityManager.persist(friendRequest);      
   }
   
   @Remove @Destroy
   public void destroy() { }    
}
