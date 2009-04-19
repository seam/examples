package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

@Entity
@Name("blogComment")
public class BlogComment implements Serializable
{
   private static final long serialVersionUID = 5495139096911872039L;

   private static SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' hh:mm a");   
   
   private Integer commentId;
   private MemberBlog blog;
   private Member commentor;
   private Date commentDate;
   private String comment;
   
   @Id @GeneratedValue
   public Integer getCommentId()
   {
      return commentId;
   }
   
   public void setCommentId(Integer commentId)
   {
      this.commentId = commentId;
   }   
   
   @ManyToOne
   @JoinColumn(name = "BLOG_ID")
   public MemberBlog getBlog()
   {
      return blog;
   }
   
   public void setBlog(MemberBlog blog)
   {
      this.blog = blog;
   }
   
   @NotNull
   public String getComment()
   {
      return comment;
   }
   public void setComment(String comment)
   {
      this.comment = comment;
   }
   
   @NotNull
   public Date getCommentDate()
   {
      return commentDate;
   }
   
   public void setCommentDate(Date commentDate)
   {
      this.commentDate = commentDate;
   }
   
   @Transient
   public String getFormattedCommentDate()
   {
     return df.format(commentDate);  
   }

   @ManyToOne
   @JoinColumn(name = "COMMENTOR_ID")
   public Member getCommentor()
   {
      return commentor;
   }
   
   public void setCommentor(Member commentor)
   {
      this.commentor = commentor;
   }
  
   
}
