package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;

/**
 * This component is used to create new auctions, and is invoked via both the
 * web interface and the AuctionService web service. 
 *  
 * @author Shane Bryzak
 */
@Conversational
@Scope(CONVERSATION)
@Name("auctionAction")
@Restrict("#{identity.loggedIn}")
public class AuctionAction implements Serializable
{
   private static final long serialVersionUID = -6738397725125671313L;
   
   private static final int DEFAULT_AUCTION_DURATION = 7;
   
   @In EntityManager entityManager;
   
   @In Account authenticatedAccount;
   
   @In(create = true) AuctionEndAction auctionEnd;

   private Auction auction;
   
   private int durationDays;
   
   private List<AuctionImage> images = new ArrayList<AuctionImage>();
   private byte[] imageData;
   private String imageContentType;
   private boolean primaryImage;
   
   @Begin(join = true)
   @SuppressWarnings("unchecked")
   public void createAuction()
   {
      if (auction == null)
      {
         auction = new Auction();
         auction.setAccount(authenticatedAccount);
         auction.setStatus(Auction.STATUS_UNLISTED);
         auction.setStartingPrice(0.01);
        
         durationDays = DEFAULT_AUCTION_DURATION;
      }
   }   
   
   public void setDetails(String title, String description, int categoryId)
   {
      auction.setTitle(title);
      auction.setDescription(description);
      auction.setCategory(entityManager.find(Category.class, categoryId));      
   }
   
   /**
    * Allows the auction duration to be overidden from the default
    * 
    * @param days Number of days to set the auction duration to.
    */
   public void setDuration(int days)
   {
      this.durationDays = days;
   }
   
   public int getDuration()
   {
      return durationDays;
   }
   
   public void uploadImage()
   {
      if (imageData == null || imageData.length == 0)
      {
         FacesMessages.instance().add("No image selected");
      }
      else
      {
         AuctionImage img = new AuctionImage();
         img.setAuction(auction);
         img.setData(imageData);
         img.setContentType(imageContentType);
         if (auction.getImage() == null || primaryImage)
           auction.setImage(img);
         images.add(img);
         
         imageData = null;
         imageContentType = null;
      }
   }
   
   @End
   public void confirm()
   {      
      AuctionImage temp = auction.getImage();
      auction.setImage(null);
      
      Calendar cal = new GregorianCalendar(); 
      cal.add(Calendar.DAY_OF_MONTH, durationDays);
      auction.setEndDate(cal.getTime());
      auction.setStatus(Auction.STATUS_LIVE);
      entityManager.persist(auction);
      
      for (AuctionImage img : images)
      {
         entityManager.persist(img);
      }
      
      auction.setImage(temp);
      entityManager.merge(auction);
      
      // End the auction at the correct time
      auctionEnd.endAuction(auction.getAuctionId(), auction.getEndDate());
   }

   public Auction getAuction()
   {
      return auction;
   }
   
   public void setAuction(Auction auction)
   {
      this.auction = auction;
   }
   
   public Integer getCategoryId()
   {
      return auction.getCategory() != null ? auction.getCategory().getCategoryId() : null;
   }
   
   public void setCategoryId(Integer categoryId)
   {
      auction.setCategory(entityManager.find(Category.class, categoryId));
   }
   
   public void setImageData(byte[] imageData)
   {
      this.imageData = imageData;
   }
   
   public void setImageContentType(String contentType)
   {
      this.imageContentType = contentType;
   }
   
   public void setPrimaryImage(boolean primary)
   {
      this.primaryImage = primary;
   }
   
   public List<AuctionImage> getImages()
   {
      return images;
   }
}
