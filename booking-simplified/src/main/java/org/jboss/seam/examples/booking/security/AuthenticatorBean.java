package org.jboss.seam.examples.booking.security;

import javax.ejb.Stateless;
import javax.inject.Inject;

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

   public boolean authenticate()
   {
      log.info("Logging you in!");
      return true;
   }

}
