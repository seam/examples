package org.jboss.seam.example.seamspace;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.management.JpaIdentityStore;

@Name("authenticationEvents")
public class AuthenticationEvents
{
   @Observer(JpaIdentityStore.EVENT_USER_AUTHENTICATED)
   public void loginSuccessful(MemberAccount account)
   {
      Contexts.getSessionContext().set("authenticatedMember", account.getMember());
   }
}
