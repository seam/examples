package org.jboss.seam.example.restbay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.NotNull;

@Entity
public class Bid implements Serializable
{
   private static final long serialVersionUID = -6214050645831251660L;
   
   private Integer bidId;
   private Auction auction;
   private Account account;
   private Date bidDate;
   private double maxAmount;
   private double actualAmount;
   
   @Id @GeneratedValue
   public Integer getBidId()
   {
      return bidId;
   }
   
   public void setBidId(Integer bidId)
   {
      this.bidId = bidId;
   }
   
   @NotNull
   @ManyToOne
   public Auction getAuction()
   {
      return auction;
   }
   
   public void setAuction(Auction auction)
   {
      this.auction = auction;
   }
   
   @NotNull
   public Account getAccount()
   {
      return account;
   }
   
   public void setAccount(Account account)
   {
      this.account = account;
   }
   
   @NotNull
   public Date getBidDate()
   {
      return bidDate;
   }
   
   public void setBidDate(Date bidDate)
   {
      this.bidDate = bidDate;
   }
   
   public double getMaxAmount()
   {
      return maxAmount;
   }
   
   public void setMaxAmount(double maxAmount)
   {
      this.maxAmount = maxAmount;
   }
   
   public double getActualAmount()
   {
      return actualAmount;
   }
   
   public void setActualAmount(double actualAmount)
   {
      this.actualAmount = actualAmount;
   }
}

