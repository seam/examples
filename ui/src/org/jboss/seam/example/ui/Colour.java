package org.jboss.seam.example.ui;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Colour implements Serializable
{
   
   @ManyToMany(mappedBy="favouriteColours")
   private List<Person> people;

   @Id @GeneratedValue
   private Integer id;
   
   private String name;

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
