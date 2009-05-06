package org.jboss.seam.examples.booking.account;

import javax.annotation.Named;
import javax.context.SessionScoped;
import javax.ejb.Stateless;
import javax.inject.Current;
import javax.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logger;

/**
 * @author Dan Allen
 */
public
@Stateless
class AccountProducerBean implements AccountProducer
{
   private @Logger Log log;

   @PersistenceContext EntityManager em;

   @Current Identity identity;
   
   @Current Credentials credentials;

   public
   @Produces
   @Registered
   @Named("user")
   @SessionScoped
   User getCurrentAccount()
   {
      if (identity.isLoggedIn())
      {
         log.info("Producing user from username {0}", credentials.getUsername());
         User candidate = em.find(User.class, credentials.getUsername());
         if (candidate != null)
         {
            return new User(candidate.getName(), candidate.getUsername());
         }
      }

      log.info("Producing dummy User");
      // TODO can't return null, but then we are not honoring the semantics of our binding type
      return new User();
   }
}
