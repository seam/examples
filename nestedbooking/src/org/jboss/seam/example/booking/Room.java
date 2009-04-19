package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.jboss.seam.annotations.Name;

@Entity
@Name("room")
public class Room implements Serializable {
   
   private Long id;
   private String name;
   private String description;
   private boolean included;
   private BigDecimal price;

   @Id @GeneratedValue
   public Long getId() 
   {
      return id;
   }

   public void setId(Long id) 
   {
      this.id = id;
   }

   @Length(max=20)
   public String getName() 
   {
      return name;
   }

   public void setName(String name) 
   {
      this.name = name;
   }

   public String getDescription() 
   {
      return description;
   }

   public void setDescription(String description) 
   {
      this.description = description;
   }

   public boolean isIncluded() 
   {
      return included;
   }

   public void setIncluded(boolean included) 
   {
      this.included = included;
   }

   public BigDecimal getPrice() 
   {
      return price;
   }

   public void setPrice(BigDecimal price) 
   {
      this.price = price;
   }	

   @Transient
   public BigDecimal getPrice(int numNights) 
   {
      return price.multiply(new BigDecimal(numNights));
   }
}
