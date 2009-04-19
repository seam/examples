package org.jboss.seam.example.ui;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.jboss.seam.annotations.Name;

@Entity
@Name("memberImage")
public class Picture implements Serializable
{
   private static final long serialVersionUID = -8088455267213832920L;
   
   @Id @GeneratedValue
   private Integer id;
   
   @OneToOne(mappedBy="picture")
   private Person person;
   private byte[] data;
   private String contentType;
   private String fileName;
   
  
   public Integer getId()
   {
      return id;
   }
   
   public void setId(Integer imageId)
   {
      this.id = imageId;
   }
   
   
   public Person getPerson()
   {
      return person;
   }
   
   public void setPerson(Person person)
   {
      this.person = person;
   }

   public String getContentType()
   {
      return contentType;
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
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
   
   public String getFileName()
   {
      return fileName;
   }
   
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }
}
