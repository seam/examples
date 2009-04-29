/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * $Id$
 */
package org.jboss.seam.examples.booking.model;

import static javax.persistence.TemporalType.DATE;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;

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
   @Temporal(DATE)
   public Date getCheckinDate()
   {
      return checkinDate;
   }

   public void setCheckinDate(Date datetime)
   {
      this.checkinDate = datetime;
   }

   @NotNull
   @ManyToOne
   public Hotel getHotel()
   {
      return hotel;
   }

   public void setHotel(Hotel hotel)
   {
      this.hotel = hotel;
   }

   @NotNull
   @ManyToOne
   public User getUser()
   {
      return user;
   }

   public void setUser(User user)
   {
      this.user = user;
   }

   @NotNull
   @Temporal(TemporalType.DATE)
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

   @Transient
   public String getDescription()
   {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      return hotel == null ? null : hotel.getName() +
         ", " + df.format(getCheckinDate()) +
         " to " + df.format(getCheckoutDate());
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

   @Override
   public String toString()
   {
      return "Booking(" + user + ", " + hotel + ")";
   }
}
