package org.jboss.seam.examples.booking.security;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.security.Credentials;
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
   @Inject private Logger log;

   @PersistenceContext private EntityManager em;

   @Inject private Credentials credentials;

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
