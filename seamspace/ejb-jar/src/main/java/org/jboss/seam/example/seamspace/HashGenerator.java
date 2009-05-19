package org.jboss.seam.example.seamspace;

import javax.annotation.Named;
import javax.context.RequestScoped;
import javax.inject.Current;

import org.jboss.seam.security.crypto.BinTools;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.jboss.seam.security.management.PasswordHash;

@Named
@RequestScoped
public class HashGenerator
{
   @Current JpaIdentityStore identityStore;
   @Current PasswordHash passwordHash;
   
   private String password;
   private String hash;
   private String passwordSalt;
   
   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      this.password = password;
   }
   
   public String getHash()
   {
      return hash;
   }
   
   public void setHash(String hash)
   {
      this.hash = hash;
   }
   
   public String getPasswordSalt()
   {
      return passwordSalt;
   }
   
   public void setPasswordSalt(String passwordSalt)
   {
      this.passwordSalt = passwordSalt;
   }
   
   public void generate()
   {
      byte[] salt;
      
      if (passwordSalt == null || "".equals(passwordSalt.trim()))
      {
         salt = passwordHash.generateRandomSalt();
         passwordSalt = BinTools.bin2hex(salt);
      }
      else
      {
         salt = BinTools.hex2bin(passwordSalt);
      }
      
      hash = identityStore.generatePasswordHash(password, salt);
   }
   
   public String getSql()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("INSERT INTO USER_ACCOUNT (username, password_hash, password_salt) values ('johnsmith', '");
      sb.append(hash);
      sb.append("', '");
      sb.append(passwordSalt);
      sb.append("');");      
      return sb.toString();
   }
}
