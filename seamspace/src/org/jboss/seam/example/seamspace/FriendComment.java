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

import org.jboss.seam.annotations.Name;

@Entity
@Name("friendComment")
public class FriendComment implements Serializable
{
   private static final long serialVersionUID = -288494386341008371L;

   private static SimpleDateFormat df = new SimpleDateFormat("d MMMM yyyy hh:mm a");   
   
   private Integer id;
   private Member member;
   private Member friend;
   private Date commentDate;
   private String comment;
   
   @Id @GeneratedValue
   public Integer getId()
   {
      return id;
   }
   
   public void setId(Integer id)
   {
      this.id = id;
   }   
   
   public String getComment()
   {
      return comment;
   }
   
   public void setComment(String comment)
   {
      this.comment = comment;
   }
   
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
   @JoinColumn(name = "FRIEND_ID")
   public Member getFriend()
   {
      return friend;
   }
   
   public void setFriend(Member friend)
   {
      this.friend = friend;
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

}
