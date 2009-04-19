//$Id$
package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

@Entity
@Name("hotel")
public class Hotel implements Serializable
{
   private Long id;
   private String name;
   private String address;
   private String city;
   private String state;
   private String zip;
   private String country;
   private List<Room> rooms;
   
   Hotel() {
	   this.rooms = new ArrayList<Room>();
   }
   
   @Id @GeneratedValue
   public Long getId()
   {
      return id;
   }
   public void setId(Long id)
   {
      this.id = id;
   }
   
   @Length(max=50) @NotNull
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   
   @Length(max=100) @NotNull
   public String getAddress()
   {
      return address;
   }
   public void setAddress(String address)
   {
      this.address = address;
   }
   
   @Length(max=40) @NotNull
   public String getCity()
   {
      return city;
   }
   public void setCity(String city)
   {
      this.city = city;
   }
   
   @Length(min=4, max=6) @NotNull
   public String getZip()
   {
      return zip;
   }
   public void setZip(String zip)
   {
      this.zip = zip;
   }
   
   @Length(min=2, max=10) @NotNull
   public String getState()
   {
      return state;
   }
   public void setState(String state)
   {
      this.state = state;
   }
   
   @Length(min=2, max=40) @NotNull
   public String getCountry()
   {
      return country;
   }
   public void setCountry(String country)
   {
      this.country = country;
   }

   @Transient
   public BigDecimal getMinPrice()
   {
      return this.getStandardRoom().getPrice();
   }
   
   @Transient
   public BigDecimal getMaxPrice()
   {
      BigDecimal maxPrice = this.getRooms().get(0).getPrice();

      for(int i = 1; i < this.getRooms().size(); i++) 
      {
         Room room = this.getRooms().get(i);

         if(maxPrice.compareTo(room.getPrice()) < 0) 
         {
            maxPrice = room.getPrice();
         }
      }

      return maxPrice;
   }
   
   @OneToMany
   @JoinColumn(name="HOTEL_ID")
   public List<Room> getRooms() 
   {
      return rooms;
   }
   
   public void setRooms(List<Room> roomsSelections) 
   {
      this.rooms = roomsSelections;
   }

   @Transient
   public List<Room> getAvailableRooms(Date checkinDate, Date checkoutDate) 
   {
      // checking could be performed here to determine which
      // rooms are available for the provided dates. to simplify
      // the example, just return the list of rooms

      return rooms;
   }
   
   @Transient
   public Room getStandardRoom() 
   {
      for(Room room : this.getRooms()) 
      {
         if(room.isIncluded()) 
         {
            return room;
         }
      }
 	  
 	  return null;
   }
   
   @Override
   public String toString()
   {
      return "Hotel(" + name + "," + address + "," + city + "," + zip + ")";
   }
}
