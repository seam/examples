package org.jboss.seam.example.seambay;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("auctionDetail")
public class AuctionDetailAction
{
   @In EntityManager entityManager;
   
   @In(required = false) Account authenticatedAccount;
   
   private int selectedAuctionId;
   
   private Auction auction;
   
   private String status;
   
   @SuppressWarnings("unchecked")
   @Factory("auction")
   public Auction getAuction()
   {
      auction = entityManager.find(Auction.class, selectedAuctionId);
      
      if (authenticatedAccount != null)
      {
         List<Bid> bids = entityManager.createQuery(
         "from Bid b where b.auction = :auction")
         .setParameter("auction", auction)
         .getResultList();
   
         boolean isBidder = false;
         
         for (Bid b : bids)
         {
            if (b.getAccount().equals(authenticatedAccount))
            {
               isBidder = true;
               break;
            }
         }
         
         if (isBidder)
         {
            status = auction.getHighBid().getAccount().equals(authenticatedAccount) ?
                  "highBidder" : "outbid";
         }
         
      }
      
      return auction;
   }
   
   public int getSelectedAuctionId()
   {
      return selectedAuctionId;
   }
   
   public void setSelectedAuctionId(int selectedAuctionId)
   {
      this.selectedAuctionId = selectedAuctionId;
   }
   
   public String getStatus()
   {
      return status;
   }
}
