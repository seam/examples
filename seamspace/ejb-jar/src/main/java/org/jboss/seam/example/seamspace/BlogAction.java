package org.jboss.seam.example.seamspace;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.inject.Produces;
import javax.persistence.EntityManager;

@Named("blog")
@ConversationScoped
public class BlogAction
{    
   private String name;   
   private Integer blogId;
   
   @Current EntityManager entityManager;
   
   @Current Member authenticatedMember;
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
