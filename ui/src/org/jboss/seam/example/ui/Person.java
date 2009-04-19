package org.jboss.seam.example.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.validator.Length;

@Entity
public class Person implements Serializable
{
   
   public enum Honorific {
      
      MR("Mr."), 
      MRS("Mrs."), 
      MISS("Miss."), 
      MS("Ms."),
      DOCTOR("Dr.");
      
      private String label;
      
      Honorific(String label)
      {
         this.label = label;
      }
      
      public String getLabel()
      {
         return label;
      }      
   }
   
   public enum Role
   {
      USER,
      ADMIN,
      MANAGER,
      SUPERADMIN;
      
      public String getName()
      {
         return this.name();
      }
   }
   
   @Id @GeneratedValue
   private Integer id;
   
   @Length(min=5)
   private String name;
   
   // A wikitext string
   private String hobbies;
   
   @ManyToOne
   private Country country;
   
   @ManyToOne
   private Continent continent;
   
   @Enumerated(EnumType.STRING)
   private Honorific honorific;
   
   private int age;
   
   @ManyToMany
   private List<Colour> favouriteColours;
   
   @ManyToOne(fetch=FetchType.LAZY)
   private Book favouriteBook;
   
   @OneToOne(cascade=CascadeType.ALL)
   private Picture picture;
   
   @CollectionOfElements
   private List<Role> roles = new ArrayList<Role>();
   
   private String pet;
   
   
   
   @ManyToOne
   private Film favouriteFilm;
   
   public Person()
   {
      picture = new Picture();
   }
   
   public List<Role> getRoles()
   {
      return roles;
   }
   
   public void setRoles(List<Role> roles)
   {
      this.roles = roles;
   }

   public Country getCountry()
   {
      return country;
   }

   public void setCountry(Country country)
   {
      this.country = country;
   }

   public Integer getId()
   {
      return id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   public int getAge()
   {
      return age;
   }
   
   public void setAge(int age)
   {
      this.age = age;
   }
   
   public Continent getContinent()
   {
      return continent;
   }
   
   public void setContinent(Continent continent)
   {
      this.continent = continent;
   }
   
   public Honorific getHonorific()
   {
      return honorific;
   }
   
   public void setHonorific(Honorific honorific)
   {
      this.honorific = honorific;
   }
   
   public List<Colour> getFavouriteColours()
   {
      return favouriteColours;
   }
   
   public void setFavouriteColours(List<Colour> favouriteColours)
   {
      this.favouriteColours = favouriteColours;
   }
   
   public Book getFavouriteBook()
   {
      return favouriteBook;
   }
   
   public void setFavouriteBook(Book favouriteBook)
   {
      this.favouriteBook = favouriteBook;
   }
   
   public String getHobbies()
   {
      return hobbies;
   }
   
   public void setHobbies(String hobbies)
   {
      this.hobbies = hobbies;
   }
   
   public Picture getPicture()
   {
      if (picture == null)
      {
         picture = new Picture();
      }
      return picture;
   }
   
   public void setPicture(Picture picture)
   {
      this.picture = picture;
   }
   
   public String getPet()
   {
      return pet;
   }
   
   public void setPet(String pet)
   {
      this.pet = pet;
   }
   
   public Film getFavouriteFilm()
   {
      return favouriteFilm;
   }
   
   public void setFavouriteFilm(Film favouriteFilm)
   {
      this.favouriteFilm = favouriteFilm;
   }
}
