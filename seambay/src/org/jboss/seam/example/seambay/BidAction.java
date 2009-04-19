package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;

@Scope(CONVERSATION)
@Name("bidAction")
public class BidAction
{   
   @In EntityManager entityManager;
   
   private Bid bid;
   
   @In(required = false) @Out(required = false)
   private Auction auction;
   
   @In(required = false)
   private Account authenticatedAccount;
   
   private String outcome;
   
   @Begin(join = true)
   public void placeBid()
   {
      if (auction.getStatus() != Auction.STATUS_LIVE ||
           auction.getEndDate().getTime() < System.currentTimeMillis())
      {
         outcome = "ended";
      }
      else
      {
         bid = new Bid();
         bid.setAuction(auction);
         
         updateBid();
      }
   }
   
   public void updateBid()
   {
      String amt = Contexts.getEventContext().isSet("bidAmount") ?
            Contexts.getEventContext().get("bidAmount").toString() : null;
      
      if (amt != null)
      {
         double amount = Double.parseDouble(amt.toString());            
         if (amt != null && amount >= bid.getAuction().getRequiredBid())
         {        
            bid.setMaxAmount(amount);
            outcome = "confirm";
         }      
         else
         {
            outcome = "invalid";
         }               
      }
      else
      {
         outcome = "required";
      }
   }
   
   @SuppressWarnings("unchecked")
   public String confirmBid()
   {
      // We set the user here because the user may not be authenticated when placeBid() is called. 
      bid.setAccount(authenticatedAccount);      
      bid.setBidDate(new Date());
      
      // This is where the tricky bidding logic happens
      
      if (!entityManager.contains(bid.getAuction()))
      {
         bid.setAuction(entityManager.find(Auction.class, bid.getAuction().getAuctionId()));
      }
      
      entityManager.lock(bid.getAuction(), LockModeType.WRITE);
      entityManager.refresh(bid.getAuction());
      
      if (bid.getAuction().getStatus() != Auction.STATUS_LIVE)
      {
         outcome = "ended";
         return outcome;
      }
      else if (bid.getAuction().getEndDate().getTime() < bid.getBidDate().getTime())
      {
         bid.getAuction().setStatus(Auction.STATUS_COMPLETED);
         outcome = "ended";
         return outcome;
      }
            
      List<Bid> bids = entityManager.createQuery(
            "from Bid b where b.auction = :auction")
          .setParameter("auction", bid.getAuction())
          .getResultList();
      
      Bid highBid = null;
      
      for (Bid b : bids)
      {
         if (highBid == null)
         {
            highBid = b;
         }
         else if (b.getMaxAmount() > highBid.getMaxAmount())
         {
            highBid.setActualAmount(highBid.getMaxAmount());
            b.setActualAmount(Auction.getRequiredBid(highBid.getMaxAmount()));
            highBid = b;
         }
         else if (b.getMaxAmount() == highBid.getMaxAmount() &&
                  b.getBidDate().getTime() < highBid.getBidDate().getTime())
         {
            highBid.setActualAmount(highBid.getMaxAmount());
            b.setActualAmount(highBid.getMaxAmount());
            highBid = b;
         }
      }
      
      if (highBid == null)
      {
         // There are no bids so far...
         bid.setActualAmount(bid.getAuction().getRequiredBid());
         bid.getAuction().setHighBid(bid);
         outcome = "success";
      }
      else if (bid.getMaxAmount() > highBid.getMaxAmount())
      {
         // If this bid is higher than the previous maximum bid, and is from
         // a different bidder, set the actual amount to the next required bid 
         // amount for the auction
         if (!bid.getAccount().equals(highBid.getAccount()))
         {
            bid.setActualAmount(Auction.getRequiredBid(highBid.getMaxAmount()));
         }        
         else
         {
            // Otherwise don't change the amount from the bidder's last bid
            bid.setActualAmount(highBid.getActualAmount());
         }
         bid.getAuction().setHighBid(bid);         
         outcome = "success";
      }
      else
      {
         if (!bid.getAccount().equals(highBid.getAccount()))
         {
            // Set this bid, and the highest bid's, actual bid amount to this
            // bid's maximum amount
            highBid.setActualAmount(bid.getMaxAmount());
            bid.setActualAmount(bid.getMaxAmount());
            outcome = "outbid";
         }
         else
         {
            outcome = "invalid";
         }
      }
                        
      if ("success".equals(outcome) || "outbid".equals(outcome)) 
      {         
         bid.getAuction().setBids(bid.getAuction().getBids() + 1);
         
         entityManager.persist(bid);        
         entityManager.flush();        
         
         if ("success".equals(outcome))
         {
            Conversation.instance().end();
         }
         else
         {
            Bid newBid = new Bid();
            newBid.setAuction(bid.getAuction());
            newBid.setMaxAmount(bid.getMaxAmount());
            bid = newBid;
         }
      }
      
      return outcome;
   }
      
   public String getOutcome()
   {
      return outcome;
   }
   
   public Bid getBid()
   {
      return bid;
   }   
   
   public Auction getAuction()
   {
      return auction;
   }
}
