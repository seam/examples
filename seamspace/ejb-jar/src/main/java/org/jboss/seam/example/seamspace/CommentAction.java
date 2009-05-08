package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.BlogComment;
import org.jboss.seam.example.seamspace.model.Member;
import org.jboss.seam.example.seamspace.model.MemberBlog;
import org.jboss.seam.security.Secure;
import org.jboss.seam.security.annotations.Insert;

@Named
@ConversationScoped
@Secure
public class CommentAction implements Serializable
{
   private static final long serialVersionUID = -4681499577462031028L;

   @PersistenceContext EntityManager entityManager;
   
   private BlogComment comment;     
   
   /*@Current*/ Member authenticatedMember;
   
   @Current MemberBlog selectedBlog;
   
   @Current Conversation conversation;
   
   @Insert(BlogComment.class) 
   public void createComment()
   { 
      conversation.begin();
      comment = new BlogComment();
      comment.setCommentor(authenticatedMember);              
      comment.setBlog(selectedBlog);
   }
   
   public void saveComment()
   {      
      comment.setCommentDate(new Date());
      entityManager.persist(comment);
            
      entityManager.refresh(selectedBlog);
      
      conversation.end();
   }    
   
   public BlogComment getComment()
   {
      return comment;
   }
}
