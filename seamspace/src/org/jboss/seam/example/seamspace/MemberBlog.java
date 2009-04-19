package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.security.Restrict;

@Entity
@Name("memberBlog")
public class MemberBlog implements Serializable
{
   private static final long serialVersionUID = 7824113911888715595L;
   
   private static SimpleDateFormat df = new SimpleDateFormat("EEEE, MMMM d, yyyy - hh:mm a");
   
   private Integer blogId;
   private Member member;
   private Date entryDate;
   private String title;
   private String text;
   
   private List<BlogComment> comments;
   
   /**
    * This is an example of a security restriction.  Any attempts to persist a
    * new memberBlog instance requires the user to pass a permission check.  In 
    * this case, because the method is annotated with <code>@PrePersist</code> 
    * the required permission is memberBlog:insert    
    */
   @PrePersist @Restrict
   public void prePersist() {}
   
   @Id @GeneratedValue
   public Integer getBlogId()
   {
      return blogId;
   }
   
   public void setBlogId(Integer blogId)
   {
      this.blogId = blogId;
   }

   public Date getEntryDate()
   {
      return entryDate;
   }

   public void setEntryDate(Date entryDate)
   {
      this.entryDate = entryDate;
   }
   
   @Transient
   public String getFormattedEntryDate()
   {
      return df.format(entryDate);
   }

   @ManyToOne
   @JoinColumn(name = "MEMBER_ID")   
   public Member getMember()
   {
      return member;
   }

   public void setMember(Member member)
   {
      this.member = member;
   }

   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   public String getTitle()
   {
      return title;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }
   
   @OneToMany(mappedBy = "blog")
   public List<BlogComment> getComments()
   {
      return comments;
   }
   
   public void setComments(List<BlogComment> comments)
   {
      this.comments = comments;
   }
   
   @Transient
   public List<BlogComment> getSortedComments()
   {
      Collections.sort(comments, new Comparator<BlogComment>() {
         public int compare(BlogComment o1, BlogComment o2) {
            return (int) (o1.getCommentDate().getTime() - o2.getCommentDate().getTime());
         }
      });
      
      return comments;
   }
   
   @Transient
   public int getCommentCount()
   {
      return comments.size();
   }
}
