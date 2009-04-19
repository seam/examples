package org.jboss.seam.example.ui;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Country implements Serializable
{
   
   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   @ManyToOne
   private Continent continent;

   public Continent getContinent()
   {
      return continent;
   }

   public void setContinent(Continent continent)
   {
      this.continent = continent;
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
