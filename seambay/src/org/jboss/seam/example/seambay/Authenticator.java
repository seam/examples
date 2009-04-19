package org.jboss.seam.example.seambay;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator
{
   @In
   private EntityManager entityManager;
     
   @In
   private Identity identity;

   public boolean authenticate() 
   {
      try
      {            
         User user = (User) entityManager.createQuery(
            "from User where username = :username and password = :password")
            .setParameter("username", identity.getUsername())
            .setParameter("password", identity.getPassword())
            .getSingleResult();
         
         Contexts.getSessionContext().set("authenticatedUser", user);
         Contexts.getSessionContext().set("authenticatedAccount", user.getAccount());
         
         return true;
      }
      catch (NoResultException ex)
      {
         return false;
      }      
   }
}
