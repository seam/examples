package org.jboss.seam.examples.booking.security;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.AnnotationLiteral;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.event.Observes;

import org.jboss.seam.examples.booking.account.Registered;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.security.events.LoggedInEvent;
import org.jboss.seam.security.events.LoggedOutEvent;
import org.jboss.seam.web.HttpSessionManager;
import org.jboss.webbeans.context.AbstractThreadLocalMapContext;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logger;

/**
 * An observer that monitors authentication events and performs
 * initialization and cleanup.
 * 
 * @author Dan Allen
 */
public
@RequestScoped
class AuthenticationEventListener
{
   private @Logger Log log;
   
   /**
    * Clear the dummy register user when a login event occurs. Temporary workaround
    * for not being able to clear this out some other way.
    */
   public void onLogin(@Observes LoggedInEvent loggedInEvent, BeanManager manager)
   {
      log.info(loggedInEvent.getPrincipal().getName() + " has logged in; clearing instance of @Registered User");
      Bean<User> registeredUserBean = manager.getBeans(User.class, new AnnotationLiteral<Registered>() {}).iterator().next();
      Context sessionContext = manager.getContext(SessionScoped.class);
      ((AbstractThreadLocalMapContext) sessionContext).getBeanStore().remove(registeredUserBean);
   }
   
   /**
    * Observe the logout event and prepare the session to be terminated. We
    * cannot terminate the session immediately or else it will cause any
    * additional session-scoped observers to fail. It's necessary to delegate
    * this task to the Seam HttpSessionManager, which can terminate (i.e.,
    * invalidate) the session when the current request ends.
    */
   public void onLogout(@Observes LoggedOutEvent loggedOutEvent, HttpSessionManager sessionManager)
   {
      log.info(loggedOutEvent.getPrincipal().getName() + " has logged out");
      sessionManager.invalidateAtEndOfRequest();
   }
   
}
