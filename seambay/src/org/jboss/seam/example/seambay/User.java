package org.jboss.seam.example.seambay;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User implements Serializable
{   
   private static final long serialVersionUID = 1L;
   
   private Integer userId;
   private String username;
   private String password;
   private Account account;
   
   @Id @GeneratedValue
   public Integer getUserId()
   {
      return userId;
   }
   
   public void setUserId(Integer userId)
   {
      this.userId = userId;
   }
   
   @NotNull
   @Length(min = 3, max = 40)
   @Pattern(regex="[a-zA-Z]?[a-zA-Z0-9]+", 
         message="Username must start with a letter, and only contain letters or numbers")   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
   }

   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      this.password = password;
   }
   
   @OneToOne
   @NotNull
   @JoinColumn(name = "ACCOUNT_ID")
   public Account getAccount()
   {
      return account;
   }
   
   public void setAccount(Account account)
   {
      this.account = account;
   }
}
