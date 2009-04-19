package org.jboss.seam.example.contactlist;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Version;

import org.hibernate.validator.Length;

@Entity
public class Contact
{
   @Id @GeneratedValue 
   private Long id;
   
   @Length(max=50)
   private String firstName;
   @Length(max=50)
   private String lastName;
   @Length(max=250)
   private String address;
   @Length(max=50)
   private String city;
   @Length(max=50)
   private String state;
   @Length(max=6)
   private String zip;
   @Length(max=50)
   private String country;
   @Length(max=20)
   private String homePhone;
   @Length(max=20)
   private String businessPhone;
   @Length(max=20)
   private String cellPhone;
   
   @Version
   private int version;
   
   @OneToMany(mappedBy="contact", cascade=CascadeType.REMOVE)
   @OrderBy("created")
   private List<Comment> comments = new ArrayList<Comment>();
   
   public String getAddress()
   {
      return address;
   }
   public void setAddress(String address)
   {
      this.address = address;
   }
   public String getBusinessPhone()
   {
      return businessPhone;
   }
   public void setBusinessPhone(String businessPhone)
   {
      this.businessPhone = businessPhone;
   }
   public String getCellPhone()
   {
      return cellPhone;
   }
   public void setCellPhone(String cellPhone)
   {
      this.cellPhone = cellPhone;
   }
   public String getCity()
   {
      return city;
   }
   public void setCity(String city)
   {
      this.city = city;
   }
   public String getCountry()
   {
      return country;
   }
   public void setCountry(String country)
   {
      this.country = country;
   }
   public String getFirstName()
   {
      return firstName;
   }
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }
   public String getHomePhone()
   {
      return homePhone;
   }
   public void setHomePhone(String homePhone)
   {
      this.homePhone = homePhone;
   }
   public String getLastName()
   {
      return lastName;
   }
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }
   public String getState()
   {
      return state;
   }
   public void setState(String state)
   {
      this.state = state;
   }
   public String getZip()
   {
      return zip;
   }
   public void setZip(String zip)
   {
      this.zip = zip;
   }
   public Long getId()
   {
      return id;
   }
   public void setId(Long id)
   {
      this.id = id;
   }
   public List<Comment> getComments()
   {
      return comments;
   }
}
