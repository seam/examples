package org.jboss.seam.examples.booking.security;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.slf4j.Logger;

/**
 * This implementation of <strong>Authenticator</strong> cross references the
 * values of the user's credentials against the database.
 * 
 * @author Dan Allen
 */
@Stateless
public class AuthenticatorBean implements Authenticator
{
   @Inject
   private Logger log;

   @PersistenceContext
   private EntityManager em;

   @Inject
   private Messages messages;

   @Inject
   private Credentials credentials;

   @Inject
   @Authenticated
   private Event<User> loginEventSrc;

   public boolean authenticate()
   {
      log.info("Logging in " + credentials.getUsername());
      if ((credentials.getUsername() == null) || (credentials.getPassword() == null))
      {
         messages.info(new BundleKey("messages", "identity.loginFailed"));
         return false;
      }

      User user = em.find(User.class, credentials.getUsername());
      if ((user != null) && user.getPassword().equals(credentials.getPassword()))
      {
         loginEventSrc.fire(user);
         messages.info(new BundleKey("messages", "identity.loggedIn"), user.getName());
         return true;
      }
      else
      {
         messages.info(new BundleKey("messages", "identity.loginFailed"));
         return false;
      }
   }

}
