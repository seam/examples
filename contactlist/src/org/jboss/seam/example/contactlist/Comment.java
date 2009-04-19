package org.jboss.seam.example.contactlist;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

@Entity
public class Comment
{
   @Id @GeneratedValue 
   private Long id;
   
   @NotNull @ManyToOne
   private Contact contact;
   
   @NotNull @Length(max=1500)
   private String text;
   
   @NotNull
   private Date created;

   public Contact getContact()
   {
      return contact;
   }

   public void setContact(Contact contact)
   {
      this.contact = contact;
      contact.getComments().add(this);
   }

   public Long getId()
   {
      return id;
   }

   public String getText()
   {
      return text;
   }

   public void setText(String text)
   {
      this.text = text;
   }

   public Date getCreated()
   {
      return created;
   }

   public void setCreated(Date created)
   {
      this.created = created;
   }  

}
