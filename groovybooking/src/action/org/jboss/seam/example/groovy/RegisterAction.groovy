//$Id$
package org.jboss.seam.example.groovy;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import static org.jboss.seam.ScopeType.EVENT;

@Scope(EVENT)
@Name("register")
class RegisterAction
{

   @In
   User user;

   @In
   EntityManager em;

   @In
   FacesMessages facesMessages;

   String verify;

   boolean registered;

   void register()
   {
       if ( user.password == verify ) {
          List existing = em.createQuery(
             '''select u.username
             from User u
             where u.username=#{user.username}
             ''').getResultList()

          if (!existing.size())
          {
             em.persist(user)
             facesMessages.add("Successfully registered as #{user.username}", new Object[0]);
             registered = true
          }
          else
          {
             facesMessages.addToControl("username", "Username #{user.username} already exists")
          }
       }
       else
       {
          facesMessages.add("verify", "Re-enter your password")
          verify=null
       }
   }

   void invalid()
   {
      facesMessages.add("Please try again", new Object[0])
   }
}
