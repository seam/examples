package org.jboss.seam.examples.booking.security;

import java.io.Serializable;
import javax.annotation.Named;
import javax.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Current;
import javax.inject.Initializer;
import javax.inject.manager.Manager;

/**
 * @author Dan Allen
 */
public
@Named
@SessionScoped
class Identity implements Serializable
{
   @Current Authenticator authenticator;

   @Current Manager manager;

   private Credentials credentials;

   private boolean loggedIn;

   public Identity() {}
   
   public @Initializer Identity(Credentials credentials)
   {
      this.credentials = credentials;
   }


   public boolean isLoggedIn()
   {
      return loggedIn;
   }

   public String getUsername()
   {
      return credentials.getUsername();
   }

   public void autoLogin()
   {
      loggedIn = true;
   }

   public void login()
   {
      if (authenticator.authenticate())
      {
         loggedIn = true;
         // authenticationEvent.fire(new AuthenticationEvent(credentials), new AnnotationLiteral<Success>() {});
         return;
      }

      // authenticationEvent.fire(new AuthenticationEvent(credentials), new AnnotationLiteral<Failed>() {});
   }

   public void logout()
   {
      credentials.clear();
      loggedIn = false;
      // FIXME this is a dirty hack to reset a producer
      FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

//      Set<Bean<User>> candidates = manager.resolveByType(User.class, new AnnotationLiteral<Registered>() {});
//      assert candidates.size() == 1;
//      User user = manager.getInstanceByType(User.class, new AnnotationLiteral<Registered>() {});
//      candidates.iterator().next().destroy(user);
   }
}
