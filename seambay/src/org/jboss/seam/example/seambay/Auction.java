package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.validator.NotNull;

@Entity
public class Auction implements Serializable
{
   public static final int STATUS_UNLISTED = 0;
   public static final int STATUS_LIVE = 1;
   public static final int STATUS_COMPLETED = 2;
   
   private static final long serialVersionUID = -8349473227099432431L;

   private Integer auctionId;
   private Account account;
   private Category category;
   private String title;
   private String description;
   private Date endDate;
   private AuctionImage image;
   private Bid highBid;
   private int bids;
   private double startingPrice;
   
   private int status;   
   private int version;
   
   @Id @GeneratedValue
   public Integer getAuctionId()
   {
      return auctionId;
   }
   
   public void setAuctionId(Integer auctionId)
   {
      this.auctionId = auctionId;
   }
   
   @NotNull
   @ManyToOne
   @JoinColumn(name = "ACCOUNT_ID")
   public Account getAccount()
   {
      return account;
   }
   
   public void setAccount(Account account)
   {
      this.account = account;
   }
   
   @NotNull   
   @ManyToOne
   @JoinColumn(name = "CATEGORY_ID")
   public Category getCategory()
   {
      return category;
   }
   
   public void setCategory(Category category)
   {
      this.category = category;
   }
   
   @NotNull
   public String getTitle()
   {
      return title;
   }
   
   public void setTitle(String title)
   {
      this.title = title;
   }
   
   @NotNull
   public String getDescription()
   {
      return description;
   }
   
   public void setDescription(String description)
   {
      this.description = description;
   }
   
   @NotNull
   public Date getEndDate()
   {
      return endDate;
   }
   
   public void setEndDate(Date endDate)
   {
      this.endDate = endDate;
   }
   
   @OneToOne
   @JoinColumn(name = "IMAGE_ID")
   public AuctionImage getImage()
   {
      return image;
   }
   
   public void setImage(AuctionImage image)
   {
      this.image = image;
   }
   
   @OneToOne
   public Bid getHighBid()
   {
      return highBid;
   }
   
   public void setHighBid(Bid highBid)
   {
      this.highBid = highBid;
   }
   
   public int getBids()
   {
      return bids;
   }
   
   public void setBids(int bids)
   {
      this.bids = bids;
   }
   
   @Transient
   public long getTimeLeft()
   {      
      return endDate != null ? (endDate.getTime() - System.currentTimeMillis()) : 0; 
   }   
   
   @Transient
   public String getPrettyTimeLeft()
   {
      long timeLeft = getTimeLeft() / 1000;
      
      int days = (int) Math.floor(timeLeft / (60 * 60 * 24));
      
      timeLeft -= days * 24 * 60 * 60;
      int hours = (int) Math.floor(timeLeft / (60 * 60));
      
      timeLeft -= hours * 60 * 60;
      int minutes = (int) Math.floor(timeLeft / 60);

      StringBuilder sb = new StringBuilder();
      
      if (days > 0)
         sb.append(String.format("%dd ", days));
      
      if (hours > 0)
         sb.append(String.format("%dh ", hours));

      if (minutes > 0)
         sb.append(String.format("%dm ", minutes));     
      
      return sb.toString();
   }
   
   @Transient
   public String getDaysHoursLeft()
   {
      long timeLeft = getTimeLeft() / 1000;
      
      int days = (int) Math.floor(timeLeft / (60 * 60 * 24));
      
      timeLeft -= days * 24 * 60 * 60;
      int hours = (int) Math.floor(timeLeft / (60 * 60));
      
      StringBuilder sb = new StringBuilder();
      
      if (days > 0)
         sb.append(String.format("%d days ", days));
      
      if (hours > 0)
         sb.append(String.format("%d hour", hours));
      
      if (hours > 1)
         sb.append('s');
      
      return sb.toString();      
   }
   
   public double getStartingPrice()
   {
      return startingPrice;
   }
   
   public void setStartingPrice(double startingPrice)
   {
      this.startingPrice = startingPrice;
   }
   
   public int getStatus()
   {
      return status;
   }
   
   public void setStatus(int status)
   {
      this.status = status;
   }
   
   @Version
   public int getVersion()
   {
      return version;
   }
   
   public void setVersion(int version)
   {
      this.version = version;
   }
   
   @Transient 
   public double getCurrentPrice()
   {
      return highBid != null ? highBid.getActualAmount() : getStartingPrice();
   }
   
   @Transient
   public double getRequiredBid()
   {      
      return highBid != null ? getRequiredBid(highBid.getActualAmount()) : 
         getStartingPrice();
   }
   
   /**
    * Returns the amount required to outbid the specified bid amount.
    * 
    * @param amount The current bid amount
    * @return The bid amount required to outbid the current bid
    */
   @Transient
   static double getRequiredBid(double amount)
   {
      if (amount < 100)
      {
         return Math.ceil(amount) + 1;
      }
      else if (amount < 200)
      {
         return Math.ceil(amount) + 2;
      }
      else if (amount < 500)
      {
         return Math.ceil(amount) + 5;
      }
      else if (amount < 1000)
      {
         return Math.ceil(amount) + 10;
      }
      else if (amount < 5000)
      {
         return Math.ceil(amount) + 20;
      }
      else if (amount < 20000)
      {
         return Math.ceil(amount) + 50;
      }
      else if (amount < 50000)
      {
         return Math.ceil(amount) + 100;
      }
      else if (amount < 100000)
      {
         return Math.ceil(amount) + 200;
      }
      else if (amount < 500000)
      {
         return Math.ceil(amount) + 500;
      }

      return Math.ceil(amount) + 1000;
   }
}
