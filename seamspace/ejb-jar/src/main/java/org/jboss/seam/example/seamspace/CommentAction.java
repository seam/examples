package org.jboss.seam.example.seamspace;

import java.util.Date;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.persistence.EntityManager;

import org.jboss.seam.security.Secure;
import org.jboss.seam.security.annotations.Insert;

@Named
@ConversationScoped
@Secure
public class CommentAction 
{
   @Current EntityManager entityManager;
   
   private BlogComment comment;     
   
   @Current Member authenticatedMember;
   
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
