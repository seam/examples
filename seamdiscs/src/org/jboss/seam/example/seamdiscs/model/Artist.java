package org.jboss.seam.example.seamdiscs.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Artist
{
   
   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   @OneToMany(mappedBy="artist", cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
   @OrderBy("_release")
   private List<Disc> discs;
   
   @Column(length=5000)
   private String description;
   
   public Artist()
   {
      this.discs = new ArrayList<Disc>();
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

   public List<Disc> getDiscs()
   {
      return discs;
   }

   public void setDiscs(List<Disc> releases)
   {
      this.discs = releases;
   }
   
   public String getDescription()
   {
      return description;
   }
   
   public void setDescription(String description)
   {
      this.description = description;
   }
   
}
