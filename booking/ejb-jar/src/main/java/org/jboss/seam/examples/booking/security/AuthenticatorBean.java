package org.jboss.seam.examples.booking.security;

import javax.ejb.Stateless;
import javax.inject.Current;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.security.Credentials;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logger;

/**
 * This implementation of <strong>Authenticator</strong>
 * cross references the values of the user's credentials
 * against the database.
 *
 * @author Dan Allen
 */
public
@Stateless
class AuthenticatorBean implements Authenticator
{
   private @Logger Log log;

   @PersistenceContext EntityManager em;

   @Current Credentials credentials;

   public boolean authenticate()
   {
      if (credentials.getUsername() != null && credentials.getUsername().length() > 0)
      {
         log.info("Authenticating {0}...", credentials.getUsername());
         User user = em.find(User.class, credentials.getUsername());
         if (user != null && user.getPassword().equals(credentials.getPassword()))
         {
            credentials.setPassword(null);
            return true;
         }
      }

      return false;
   }

}
