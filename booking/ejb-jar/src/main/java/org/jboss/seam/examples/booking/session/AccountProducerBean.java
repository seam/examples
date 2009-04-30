package org.jboss.seam.examples.booking.session;

import javax.annotation.Named;
import javax.context.SessionScoped;
import javax.ejb.Stateless;
import javax.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.Registered;
import org.jboss.seam.examples.booking.model.User;

/**
 * @author Dan Allen
 */
public
@Stateless
class AccountProducerBean implements AccountProducer
{
   private @PersistenceContext EntityManager em;

   public
   @Produces
//   @Registered // FIXME not resolving"user" variable if binding type is present
   @Named("user")
   @SessionScoped
   User getCurrentAccount()
   {
      // FIXME provide a real implementation
      return em.find(User.class, "dan");
   }
}
