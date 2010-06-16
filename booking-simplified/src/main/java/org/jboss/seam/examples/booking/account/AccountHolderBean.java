package org.jboss.seam.examples.booking.account;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.examples.booking.model.User;
import org.slf4j.Logger;

/**
 * @author Dan Allen
 */
@Stateful
@SessionScoped
public class AccountHolderBean implements AccountHolder
{
   private User currentUser;

   @Produces
   @Authenticated
   @Named("currentUser")
   public User getCurrentAccount()
   {
      return currentUser;
   }

   public void onLogin(@Observes @Authenticated User user)
   {
      currentUser = user;
   }
}
