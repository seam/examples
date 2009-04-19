package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.jboss.seam.annotations.Name;

/**
 * A member account
 * 
 * @author Shane Bryzak
 */
@Entity
@Name("member")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "membername"))
public class Member implements Serializable
{
   private static final long serialVersionUID = 5179242727836683375L;
   
   public enum Gender {
      male("Male", "his"), 
      female("Female", "her");
      
     private String descr;
     private String possessive;
     
     Gender(String descr, String possessive) {
       this.descr = descr;
       this.possessive = possessive;
      }
     public String getDescr() {
        return descr;
     }
     
     public String getPossessive() {
        return possessive;
     }
   };
   
   private Integer memberId;
   private String memberName;
   private String firstName;
   private String lastName;
   private String email;
   private MemberImage picture;
   
   private String tagline;
   private Gender gender;
   private Date dob;
   private String location;
   private Date memberSince;
   
   private Set<MemberImage> images;   
   private Set<MemberFriend> friends;

   @Id @GeneratedValue
   public Integer getMemberId()
   {
      return memberId;
   }

   public void setMemberId(Integer memberId)
   {
      this.memberId = memberId;
   }
   
   @NotNull
   @Length(min = 3, max = 40)
   @Pattern(regex="[a-zA-Z]?[a-zA-Z0-9_]+", 
         message="Member name must start with a letter, and only contain letters, numbers or underscores")
   public String getMemberName()
   {
      return memberName;
   }

   public void setMemberName(String memberName)
   {
      this.memberName = memberName;
   }
   
   @NotNull
   @Length(min = 3, max = 40)
   @Pattern(regex="[a-zA-Z]+", message="First name must only contain letters")
   public String getFirstName()
   {
      return firstName;
   }
   
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }
   
   @NotNull
   @Length(min = 3, max = 40)
   @Pattern(regex="[a-zA-Z]+", message="Last name must only contain letters")
   public String getLastName()
   {
      return lastName;
   }
   
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }   
   
   @NotNull @Email
   public String getEmail()
   {
      return email;
   }
   
   public void setEmail(String email)
   {
      this.email = email;
   }

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "PICTURE_ID")
   public MemberImage getPicture()
   {
      return picture;
   }

   public void setPicture(MemberImage picture)
   {
      this.picture = picture;
   }

   @NotNull
   public Date getDob()
   {
      return dob;
   }

   public void setDob(Date dob)
   {
      this.dob = dob;
   }

   @NotNull
   public Gender getGender()
   {
      return gender;
   }

   public void setGender(Gender gender)
   {
      this.gender = gender;
   }

   public String getLocation()
   {
      return location;
   }

   public void setLocation(String location)
   {
      this.location = location;
   }
   
   @NotNull
   public Date getMemberSince()
   {
      return memberSince;
   }
   
   public void setMemberSince(Date memberSince)
   {
      this.memberSince = memberSince;
   }

   public String getTagline()
   {
      return tagline;
   }

   public void setTagline(String tagline)
   {
      this.tagline = tagline;
   }

   @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
   public Set<MemberImage> getImages()
   {
      return images;
   }

   public void setImages(Set<MemberImage> images)
   {
      this.images = images;
   }
   
   @OneToMany(mappedBy = "member")
   public Set<MemberFriend> getFriends()
   {
      return friends;
   }
   
   public void setFriends(Set<MemberFriend> friends)
   {
      this.friends = friends;   
   }
   
   @Transient
   public boolean isFriend(Member member)
   {
      for (MemberFriend f : friends)
      {
         if (f.getFriend().getMemberId().equals(member.getMemberId())) return true;          
      }
      
      return false;
   }
   
   @Transient
   public String getAge()
   {
      Calendar birthday = new GregorianCalendar();
      birthday.setTime(dob);
      int by = birthday.get(Calendar.YEAR);
      int bm = birthday.get(Calendar.MONTH);
      int bd = birthday.get(Calendar.DATE);
      
      Calendar now = new GregorianCalendar();
      now.setTimeInMillis(System.currentTimeMillis());
      int ny = now.get(Calendar.YEAR);
      int nm = now.get(Calendar.MONTH);
      int nd = now.get(Calendar.DATE);      
      
      int age = ny - by + (nm > bm || (nm == bm && nd >= bd) ? 0 : -1);                              
      return String.format("%d years old", age);                              
   }
}
