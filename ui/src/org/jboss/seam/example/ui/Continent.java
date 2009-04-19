package org.jboss.seam.example.ui;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Continent implements Serializable
{
   @Id @GeneratedValue
   private Integer id;
 
   private String name;
   
   @OneToMany(mappedBy="continent")
   private List<Country> countries;

   public List<Country> getCountries()
   {
      return countries;
   }

   public void setCountries(List<Country> countries)
   {
      this.countries = countries;
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
