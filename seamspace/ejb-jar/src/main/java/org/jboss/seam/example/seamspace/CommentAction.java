package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Date;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Insert;
import org.jboss.seam.core.Conversation;

@Scope(CONVERSATION)
@Name("commentAction")
@Transactional
public class CommentAction 
{
   @In
   private EntityManager entityManager;
   
   private BlogComment comment;     
   
   @In(required = false)
   private Member authenticatedMember;
   
   @In(required = false)
   private MemberBlog selectedBlog;
   
   @Begin(nested = true) @Insert(BlogComment.class) 
   public void createComment()
   {            
      comment = new BlogComment();
      comment.setCommentor(authenticatedMember);              
      comment.setBlog(selectedBlog);
   }
   
   public void saveComment()
   {      
      comment.setCommentDate(new Date());
      entityManager.persist(comment);
            
      entityManager.refresh(selectedBlog);
      
      Conversation.instance().end();
   }    
   
   public BlogComment getComment()
   {
      return comment;
   }
}
