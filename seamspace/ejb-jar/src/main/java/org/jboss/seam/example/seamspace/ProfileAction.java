package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import javax.annotation.Named;
import javax.context.RequestScoped;
import javax.inject.Current;
import javax.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.FriendComment;
import org.jboss.seam.example.seamspace.model.Member;
import org.jboss.seam.example.seamspace.model.MemberBlog;

@Named("profile")
@RequestScoped
public class ProfileAction implements Serializable
{
   private static final long serialVersionUID = 8352519286505497298L;

   //@RequestParameter
   private String name;
   
   /*@Current */ Member selectedMember;
   
   /*@Current */ Member authenticatedMember;  
   
   @PersistenceContext EntityManager entityManager;

   //@Factory("selectedMember")
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
   public @Produces @Named("memberBlogs") List<MemberBlog> getMemberBlogs()
   {
      List<MemberBlog> memberBlogs;
      
      if (name == null && authenticatedMember != null)
      {
         name = authenticatedMember.getMemberName();
      }      
      
      memberBlogs = entityManager.createQuery(
            "from MemberBlog b where b.member.memberName = :memberName order by b.entryDate desc")
            .setParameter("memberName", name)
            .getResultList();
      
      return memberBlogs;
   }   
   
   @SuppressWarnings("unchecked")
   public @Produces @Named("newMembers") List<Member> newMembers()
   {
      List<Member> newMembers = entityManager.createQuery(
            "from Member order by memberSince desc")
            .setMaxResults(10)
            .getResultList();
      
      // Randomly select 3 of the latest 10 members
      Random rnd = new Random(System.currentTimeMillis());
      while (newMembers.size() > 3)
      {
         newMembers.remove(rnd.nextInt(newMembers.size()));
      }
      
      return newMembers;
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
}
