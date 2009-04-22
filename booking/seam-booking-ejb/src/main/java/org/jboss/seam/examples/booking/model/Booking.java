/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.jboss.seam.examples.booking.model;

import static javax.persistence.TemporalType.DATE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * <p><strong>Booking</strong> is the model/entity class that represents a hotel
 * booking.</p>
 *
 * @author Gavin King
 * @author Dan Allen
 */
public
@Entity
class Booking implements Serializable
{
   private Long id;
   private User user;
   private Hotel hotel;
   private Date checkinDate;
   private Date checkoutDate;
   private String creditCard;
   private String creditCardName;
   private int creditCardExpiryMonth;
   private int creditCardExpiryYear;
   private boolean smoking;
   private int beds;

   public Booking()
   {
   }

   public Booking(Hotel hotel, User user)
   {
      this.hotel = hotel;
      this.user = user;
   }

   @Transient
   public BigDecimal getTotal()
   {
      return hotel.getPrice().multiply(new BigDecimal(getNights()));
   }

   @Transient
   public int getNights()
   {
      return (int) (checkoutDate.getTime() - checkinDate.getTime()) / 1000 / 60 / 60 / 24;
   }

   @Id
   @GeneratedValue
   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   @NotNull
   @Basic
   @Temporal(DATE)
   public Date getCheckinDate()
   {
      return checkinDate;
   }

   public void setCheckinDate(Date datetime)
   {
      this.checkinDate = datetime;
   }

   @ManyToOne
   @NotNull
   public Hotel getHotel()
   {
      return hotel;
   }

   public void setHotel(Hotel hotel)
   {
      this.hotel = hotel;
   }

   @ManyToOne
   @NotNull
   public User getUser()
   {
      return user;
   }

   public void setUser(User user)
   {
      this.user = user;
   }

   @Basic
   @Temporal(TemporalType.DATE)
   @NotNull
   public Date getCheckoutDate()
   {
      return checkoutDate;
   }

   public void setCheckoutDate(Date checkoutDate)
   {
      this.checkoutDate = checkoutDate;
   }

   @NotNull(message = "Credit card number is required")
   @Size(min = 16, max = 16, message = "Credit card number must 16 digits long")
   @Pattern(regexp = "^\\d*$", message = "Credit card number must be numeric")
   public String getCreditCard()
   {
      return creditCard;
   }

   public void setCreditCard(String creditCard)
   {
      this.creditCard = creditCard;
   }

   @Transient
   public String getDescription()
   {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      return hotel == null ? null : hotel.getName() +
         ", " + df.format(getCheckinDate()) +
         " to " + df.format(getCheckoutDate());
   }

   public boolean isSmoking()
   {
      return smoking;
   }

   public void setSmoking(boolean smoking)
   {
      this.smoking = smoking;
   }

   public int getBeds()
   {
      return beds;
   }

   public void setBeds(int beds)
   {
      this.beds = beds;
   }

   @NotNull(message = "Credit card name is required")
   @Size(min = 3, max = 70, message = "Credit card name is required")
   public String getCreditCardName()
   {
      return creditCardName;
   }

   public void setCreditCardName(String creditCardName)
   {
      this.creditCardName = creditCardName;
   }

   public int getCreditCardExpiryMonth()
   {
      return creditCardExpiryMonth;
   }

   public void setCreditCardExpiryMonth(int creditCardExpiryMonth)
   {
      this.creditCardExpiryMonth = creditCardExpiryMonth;
   }

   public int getCreditCardExpiryYear()
   {
      return creditCardExpiryYear;
   }

   public void setCreditCardExpiryYear(int creditCardExpiryYear)
   {
      this.creditCardExpiryYear = creditCardExpiryYear;
   }

   @Override
   public String toString()
   {
      return "Booking(" + user + "," + hotel + ")";
   }
}
