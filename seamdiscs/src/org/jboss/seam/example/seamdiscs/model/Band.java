package org.jboss.seam.example.seamdiscs.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Band extends Artist
{
   
   @OneToMany(mappedBy="band", cascade=CascadeType.ALL)
   private List<BandMember> bandMembers = new ArrayList<BandMember>();

   public List<BandMember> getBandMembers()
   {
      return bandMembers;
   }

   public void setBandMembers(List<BandMember> bandMembers)
   {
      this.bandMembers = bandMembers;
   }
   
   

}
