package org.jboss.seam.example.restbay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Feedback implements Serializable
{
   private static final long serialVersionUID = -5814392910187956828L;

   public enum Rating {positive, neutral, negative};
   
   private Integer feedbackId;
   private Account forAccount;
   private Account fromAccount;
   private Date feedbackDate;
   private Auction item;
   private Rating rating;
   private String comment;
   private String response;
   
   @Id @GeneratedValue
   public Integer getFeedbackId()
   {
      return feedbackId;
   }
   
   public void setFeedbackId(Integer feedbackId)
   {
      this.feedbackId = feedbackId;
   }
   
   @ManyToOne
   @JoinColumn(name = "FOR_ACCOUNT_ID")
   public Account getForAccount()
   {
      return forAccount;
   }
   
   public void setForAccount(Account account)
   {
      this.forAccount = account;
   }
   
   @ManyToOne
   @JoinColumn(name = "FROM_ACCOUNT_ID")
   public Account getFromAccount()
   {
      return fromAccount;
   }
   
   public void setFromAccount(Account account)
   {
      this.fromAccount = account;
   }
   
   public Date getFeedbackDate()
   {
      return feedbackDate;
   }
   
   public void setFeedbackDate(Date feedbackDate)
   {
      this.feedbackDate = feedbackDate;
   }

   @OneToOne
   @JoinColumn(name = "ITEM_ID")
   public Auction getItem()
   {
      return item;
   }
   
   public void setItem(Auction item)
   {
      this.item = item;
   }
   
   public Rating getRating()
   {
      return rating;
   }
   
   public void setRating(Rating rating)
   {
      this.rating = rating;
   }
   
   public String getComment()
   {
      return comment;
   }
   
   public void setComment(String comment)
   {
      this.comment = comment;
   }
   
   public String getResponse()
   {
      return response;
   }
   
   public void setResponse(String response)
   {
      this.response = response;
   }
   
}
