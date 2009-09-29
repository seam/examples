package org.jboss.seam.examples.booking.account;

import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.inject.Named;
import javax.enterprise.inject.Produces;
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

   @Default Identity identity;
   
   @Default Credentials credentials;

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
      // TODO can't return null because this is a scoped producer, but then we are not honoring the semantics of our binding type
      return new User();
   }
}
