package org.jboss.seam.examples.booking.session;

import javax.annotation.Named;
import javax.context.SessionScoped;
import javax.ejb.Stateless;
import javax.inject.Current;
import javax.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.Registered;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.examples.booking.security.Identity;

/**
 * @author Dan Allen
 */
public
@Stateless
class AccountProducerBean implements AccountProducer
{
   @PersistenceContext EntityManager em;

   @Current Identity identity;

   public
   @Produces
   @Registered
   @Named("user")
   @SessionScoped
   User getCurrentAccount()
   {
      if (identity.isLoggedIn())
      {
         User user = em.find(User.class, identity.getUsername());
         if (user != null)
         {
            user.setPassword(null);
            return user;
         }
      }

      // TODO can't return null, but then we are not honoring the semantics of our binding type
      return new User();
   }
}
