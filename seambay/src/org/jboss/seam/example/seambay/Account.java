package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Account implements Serializable
{
   private static final long serialVersionUID = 8444287111124328025L;
   
   private Integer accountId;
   private String name;
   private int feedbackScore;
   private float feedbackPercent;
   private Date memberSince;
   private String location;
   
   @Id @GeneratedValue
   public Integer getAccountId()
   {
      return accountId;
   }
   
   public void setAccountId(Integer accountId)
   {
      this.accountId = accountId;
   }
   
   @NotNull
   @Length(min = 3, max = 40)
   @Pattern(regex="[a-zA-Z]?[a-zA-Z0-9]+", 
         message="Account name must start with a letter, and only contain letters or numbers")   
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   public int getFeedbackScore()
   {
      return feedbackScore;
   }
   
   public void setFeedbackScore(int score)
   {
      this.feedbackScore = score;
   }
   
   public float getFeedbackPercent()
   {
      return feedbackPercent;
   }
   
   public void setFeedbackPercent(float percent)
   {
      this.feedbackPercent = percent;
   }
   
   public Date getMemberSince()
   {
      return memberSince;
   }
   
   public void setMemberSince(Date memberSince)
   {
      this.memberSince = memberSince;
   }
   
   public String getLocation()
   {
      return location;
   }
   
   public void setLocation(String location)
   {
      this.location = location;
   }
   
   @Override
   public boolean equals(Object value)
   {
      if (!(value instanceof Account)) return false;
      
      Account other = (Account) value;
      
      return other.accountId != null && other.accountId.equals(this.accountId);
   }
   
   @Override
   public int hashCode()
   {
      return accountId != null ? accountId.intValue() : super.hashCode();
   }
}
