package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.BlogComment;
import org.jboss.seam.example.seamspace.model.Member;
import org.jboss.seam.example.seamspace.model.MemberBlog;

@Named("blog")
@ConversationScoped
public class BlogAction implements Serializable
{    
   private static final long serialVersionUID = 1048001063828543101L;
   
   private String name;   
   private Integer blogId;
   
   @PersistenceContext EntityManager entityManager;
   
   /*@Current */Member authenticatedMember;
   @Current Conversation conversation;
   
   private MemberBlog selectedBlog;
   
   public @Produces MemberBlog getBlog()
   {     
      return (MemberBlog) entityManager.createQuery(
        "from MemberBlog b where b.blogId = :blogId and b.member.memberName = :memberName")
        .setParameter("blogId", blogId)
        .setParameter("memberName", name)
        .getSingleResult();
   }   
   
   public void createEntry()
   {
      conversation.begin();
      selectedBlog = new MemberBlog();              
   }
   
   public void saveEntry()
   {
      selectedBlog.setMember(authenticatedMember);
      selectedBlog.setEntryDate(new Date());
      selectedBlog.setComments(new ArrayList<BlogComment>());
      
      entityManager.persist(selectedBlog);
      conversation.end();
   }
   
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   public Integer getBlogId()
   {
      return blogId;
   }
   
   public void setBlogId(Integer blogId)
   {
      this.blogId = blogId;
   }
}
