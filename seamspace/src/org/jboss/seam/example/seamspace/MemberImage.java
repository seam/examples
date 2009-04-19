package org.jboss.seam.example.seamspace;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.jboss.seam.annotations.security.permission.Permission;
import org.jboss.seam.annotations.security.permission.Permissions;

@Permissions({
   @Permission(action = "view"),
   @Permission(action = "comment")
})
@Entity
public class MemberImage implements Serializable
{
   private static final long serialVersionUID = -8088455267213832920L;
   
   private Integer imageId;
   private Member member;
   private byte[] data;
   private String contentType;
   private String caption;
   
   @Id @GeneratedValue
   public Integer getImageId()
   {
      return imageId;
   }
   
   public void setImageId(Integer imageId)
   {
      this.imageId = imageId;
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

   public String getContentType()
   {
      return contentType;
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }
   
   public String getCaption()
   {
      return caption;
   }
   
   public void setCaption(String caption)
   {
      this.caption = caption;
   }

   @Lob
   public byte[] getData()
   {
      return data;
   }

   public void setData(byte[] data)
   {
      this.data = data;
   }

}
