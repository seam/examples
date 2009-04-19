package org.jboss.seam.example.seamdiscs.action;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.TreeModel;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.example.seamdiscs.model.Artist;
import org.jboss.seam.example.seamdiscs.model.Band;
import org.jboss.seam.example.seamdiscs.model.BandMember;
import org.jboss.seam.example.seamdiscs.model.Disc;
import org.jboss.seam.framework.EntityHome;

@Name("artistHome")
@Stateful
public class ArtistHomeImpl extends EntityHome<Artist> implements ArtistHome
{
   
   @In(create=true, value="#{allArtists.resultList}")
   private List<Artist> artists;

   @Factory
   public Artist getArtist()
   {
      return super.getInstance();
   }
   
   private String type;
   
   public String getType()
   {
      return type;
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   @Override
   protected Artist createInstance()
   {
      if (Band.class.getSimpleName().equalsIgnoreCase(getType()))
      {
         return new Band();
      }
      else
      {
         return new Artist();
      }
   }
   
   public void addBandMember()
   {
      Band band = (Band) getInstance();
      band.getBandMembers().add(new BandMember(band));
   }
   
   public void addDisc()
   {
      getInstance().getDiscs().add(new Disc(getInstance()));
   }
   
   public TreeModel getTree()
   {
      return new ChildPropertyTreeModel(artists, "discs")
      {
         @Override
         protected Object getChildData(Object parentData)
         {
            if (parentData instanceof Artist)
            {
               return super.getChildData(parentData);
            }
            else
            {
               return null;
            }
         }
      };
   }
   
   // Utility method for EJB3 to cleanup, don't do anything here, so no need
   // for Seam to intercept.
   @Remove
   @BypassInterceptors
   public void ejbRemove()
   {
      
   }
}