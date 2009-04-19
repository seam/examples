package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.ScopeType.EVENT;

import java.util.List;
import java.util.Random;

import javax.ejb.Remove;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.annotations.Scope;

@Name("profile")
@Scope(EVENT)
public class ProfileAction
{
   @RequestParameter
   private String name;

   @In(required = false) @Out(required = false, scope = CONVERSATION)
   private Member selectedMember;
   
   @In(required = false)
   private Member authenticatedMember;
   
   @Out(required = false)
   List<Member> newMembers;
   
   @Out(required = false)
   List<MemberBlog> memberBlogs;   
   
   @In
   private EntityManager entityManager;

   @Factory("selectedMember")
   public void display()
   {      
      if (name == null && authenticatedMember != null)
      {
         selectedMember = (Member) entityManager.find(Member.class, 
               authenticatedMember.getMemberId());
      }
      else if (name != null)
      {
         try
         {
            selectedMember = (Member) entityManager.createQuery(
            "from Member where memberName = :memberName")
            .setParameter("memberName", name)
            .getSingleResult(); 
         }
         catch (NoResultException ex) { }
      }
   }
   
   /**
    * Returns the 5 latest blog entries for a member
    */
   @SuppressWarnings("unchecked")
   public List<MemberBlog> getLatestBlogs()
   {
      return entityManager.createQuery(
           "from MemberBlog b where b.member = :member order by b.entryDate desc")
           .setParameter("member", selectedMember)
           .setMaxResults(5)
           .getResultList();
   }

   /**
    * Used to read all blog entries for a member
    */
   @SuppressWarnings("unchecked")
   @Factory("memberBlogs")
   public void getMemberBlogs()
   {
      if (name == null && authenticatedMember != null)
      {
         name = authenticatedMember.getMemberName();
      }      
      
      memberBlogs = entityManager.createQuery(
            "from MemberBlog b where b.member.memberName = :memberName order by b.entryDate desc")
            .setParameter("memberName", name)
            .getResultList();
   }   
   
   @SuppressWarnings("unchecked")
   @Factory("newMembers")
   public void newMembers()
   {
      newMembers = entityManager.createQuery(
            "from Member order by memberSince desc")
            .setMaxResults(10)
            .getResultList();
      
      // Randomly select 3 of the latest 10 members
      Random rnd = new Random(System.currentTimeMillis());
      while (newMembers.size() > 3)
      {
         newMembers.remove(rnd.nextInt(newMembers.size()));
      }
   }
   
   @SuppressWarnings("unchecked")
   public List<Member> getFriends()
   {
      return entityManager.createQuery(
            "select f.friend from MemberFriend f where f.member = :member and authorized = true")
            .setParameter("member", selectedMember)
            .getResultList();
   }
   
   @SuppressWarnings("unchecked")
   public List<FriendComment> getFriendComments()
   {
      return entityManager.createQuery(
            "from FriendComment c where c.member = :member order by commentDate desc")
            .setParameter("member", selectedMember)
            .getResultList();
   }
   
   @Remove @Destroy
   public void destroy() { }   
}
