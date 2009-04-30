package org.jboss.seam.examples.booking.security;

import java.io.Serializable;
import javax.annotation.Named;
import javax.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.manager.Manager;

/**
 * @author Dan Allen
 */
public
@Named
@SessionScoped
class Identity implements Serializable
{
   private String username;

   private String password;

   private boolean loggedIn;

   public boolean isLoggedIn()
   {
      return loggedIn;
   }

   public void setLoggedIn(boolean loggedIn)
   {
      this.loggedIn = loggedIn;
   }

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

   public boolean login()
   {
      if (username != null && username.length() > 0)
      {
         password = null;
         loggedIn = true;
         return true;
      }

      return false;
   }

   Manager manager;

   public void logout()
   {
      username = null;
      loggedIn = false;
      // FIXME this is a dirty hack to reset a producer
      FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
   }
}
