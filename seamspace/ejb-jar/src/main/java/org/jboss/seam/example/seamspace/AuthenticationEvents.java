package org.jboss.seam.example.seamspace;

import javax.event.Observes;

import org.jboss.seam.security.events.UserAuthenticatedEvent;

public class AuthenticationEvents
{
   public void loginSuccessful(@Observes UserAuthenticatedEvent event)
   {
      //FIXME
      //Contexts.getSessionContext().set("authenticatedMember", account.getMember());
   }
}
