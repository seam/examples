//$Id: RegisterAction.java,v 1.23 2007/06/27 00:06:49 gavin Exp $
package org.jboss.seam.example.wicket.action;

import static org.jboss.seam.ScopeType.EVENT;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;

@Stateful
@Scope(EVENT)
@Name("register")
public class RegisterAction implements Register
{
   @In
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @In(create=true)
   private StatusMessages statusMessages;
   
   @In
   private String verify;
   
   private boolean registered;
   
   public void register()
   {
      if ( user.getPassword().equals(verify) )
      {
         List existing = em.createQuery("select u.username from User u where u.username=#{user.username}")
            .getResultList();
         if (existing.size()==0)
         {
            em.persist(user);
            statusMessages.addToControl("login", "Successfully registered as #{user.username}");
            registered = true;
         }
         else
         {
            statusMessages.addToControl("username", "Username #{user.username} already exists");
         }
      }
      else 
      {
         statusMessages.addToControl("verify", "Re-enter your password");
         verify=null;
      }
   }
   
   public void invalid()
   {
      statusMessages.add("Please try again");
   }
   
   public boolean isRegistered()
   {
      return registered;
   }

   
   @Remove
   public void destroy() {}
}
