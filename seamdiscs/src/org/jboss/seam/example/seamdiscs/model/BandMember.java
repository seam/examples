 package org.jboss.seam.example.seamdiscs.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class BandMember
{

   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   @ManyToOne
   private Band band;
   
   public BandMember()
   {
      
   }
   
   public BandMember(Band band)
   {
      this.band = band;
   }

   public Band getBand()
   {
      return band;
   }

   public void setBand(Band band)
   {
      this.band = band;
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
