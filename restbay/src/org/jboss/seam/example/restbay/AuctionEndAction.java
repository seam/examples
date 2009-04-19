package org.jboss.seam.example.restbay;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.Asynchronous;
import org.jboss.seam.annotations.async.Expiration;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.Date;

@Name("auctionEnd")
public class AuctionEndAction
{
   @In EntityManager entityManager;

   @Asynchronous
   @Transactional
   public void endAuction(int auctionId, @Expiration Date endDate)
   {
      System.out.println("Auction " + auctionId + " ending");

      Auction auction = entityManager.find(Auction.class, auctionId);

      entityManager.lock(auction, LockModeType.WRITE);

      auction.setStatus(Auction.STATUS_COMPLETED);

      entityManager.merge(auction);
   }
}
