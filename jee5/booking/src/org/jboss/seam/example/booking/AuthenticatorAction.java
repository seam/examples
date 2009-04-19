package org.jboss.seam.example.booking;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;

@Stateless
@Name("authenticator")
public class AuthenticatorAction implements Authenticator, Serializable
{
   @Logger private Log log;
   
   @PersistenceContext EntityManager em;
   
   @In(required = false)
   @Out(required = false, scope = SESSION)
   private User user;
   
   public boolean authenticate()
   {
      log.info("Authenticating #{identity.username}");
      List results = em.createQuery(
            "select u from User u where u.username = #{identity.username} and u.password = #{identity.password}")
            .getResultList();
      
      if ( results.size()==0 )
      {
         return false;
      }
      else
      {
         user = (User) results.get(0);
         return true;
      }
   }
   
   public boolean isUsernameAvailable()
   {
      // check if user is available in context
      // we are proving that bijection is working on inter-EJB calls
      if (user == null)
	  {
          throw new IllegalStateException("No user available in context.");
	  }

      log.info("Checking if username is available: {0}", user.getUsername());
      List results = em.createQuery(
            "select u.username from User u where u.username = :username")
            .setParameter("username", user.getUsername())
            .getResultList();
      
      if ( results.size() == 0 )
      {
         return true;
      }
      else
      {
         return false;
      }
   }

}
