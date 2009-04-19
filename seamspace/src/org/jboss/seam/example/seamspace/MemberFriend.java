package org.jboss.seam.example.seamspace;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.jboss.seam.annotations.Name;

@Entity
@Name("memberFriend")
public class MemberFriend implements Serializable
{
   private static final long serialVersionUID = -167586088947004386L;
   
   private Integer id;
   private Member member;
   private Member friend;
   
   private String introduction;
   private String response;
   
   private boolean authorized;

   @Id @GeneratedValue
   public Integer getId()
   {
      return id;
   }
   
   public void setId(Integer id)
   {
      this.id = id;
   }   
   
   public boolean isAuthorized()
   {
      return authorized;
   }
   
   public void setAuthorized(boolean authorized)
   {
      this.authorized = authorized;
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

   public String getIntroduction()
   {
      return introduction;
   }

   public void setIntroduction(String introduction)
   {
      this.introduction = introduction;
   }

   public String getResponse()
   {
      return response;
   }

   public void setResponse(String response)
   {
      this.response = response;
   }      
}
