package org.jboss.seam.example.ui;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Film implements Serializable
{
   
   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   private String director;

   public String getDirector()
   {
      return director;
   }

   public void setDirector(String director)
   {
      this.director = director;
   }

   public Integer getId()
   {
      return id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   

}
