package org.jboss.seam.example.seamspace;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.crypto.BinTools;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.jboss.seam.security.management.PasswordHash;

@Scope(ScopeType.EVENT)
@Name("hashgenerator")
public class HashGenerator
{
   @In JpaIdentityStore identityStore;
   
   private String password;
   private String passwordHash;
   private String passwordSalt;
   
   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      this.password = password;
   }
   
   public String getPasswordHash()
   {
      return passwordHash;
   }
   
   public void setPasswordHash(String passwordHash)
   {
      this.passwordHash = passwordHash;
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
         salt = PasswordHash.instance().generateRandomSalt();
         passwordSalt = BinTools.bin2hex(salt);
      }
      else
      {
         salt = BinTools.hex2bin(passwordSalt);
      }
      
      passwordHash = identityStore.generatePasswordHash(password, salt);
   }
   
   public String getSql()
   {
      StringBuilder sb = new StringBuilder();
      sb.append("INSERT INTO USER_ACCOUNT (username, password_hash, password_salt) values ('johnsmith', '");
      sb.append(passwordHash);
      sb.append("', '");
      sb.append(passwordSalt);
      sb.append("');");      
      return sb.toString();
   }
}
