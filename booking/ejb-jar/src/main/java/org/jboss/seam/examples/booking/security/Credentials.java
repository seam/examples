package org.jboss.seam.examples.booking.security;

import java.io.Serializable;
import javax.annotation.Named;
import javax.context.SessionScoped;

/**
 * Holds the user's credentials.
 *
 * @author Dan Allen
 */
public
@Named
@SessionScoped
class Credentials implements Serializable
{
   private String username;

   private String password;

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   public void clear()
   {
      this.username = null;
      this.password = null;
   }

}
