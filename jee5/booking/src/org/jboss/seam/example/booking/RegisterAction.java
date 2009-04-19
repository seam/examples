//$Id: RegisterAction.java,v 1.1 2007/06/23 18:33:59 pmuir Exp $
package org.jboss.seam.example.booking;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Stateful
@Scope(EVENT)
@Name("register")
public class RegisterAction implements Register, Serializable
{

   @In
   private User user;

   @PersistenceContext
   private EntityManager em;
   
   // if use @EJB, you don't need the ejb-local-ref defined in ejb-jar.xml,
   // but you also lose state management and client-side interceptors
   @In(create = true)
   private Authenticator authenticator;
   
   @In
   private FacesMessages facesMessages;
   
   private String verify;
   
   private boolean registered;
   
   public void register()
   {
      if ( user.getPassword().equals(verify) )
      {
         if ( authenticator.isUsernameAvailable() )
         {
            em.persist(user);
            facesMessages.add("Successfully registered as #{user.username}");
            registered = true;
         }
         else
         {
            facesMessages.add("Username #{user.username} already exists");
         }
      }
      else 
      {
         facesMessages.add("verify", "Re-enter your password");
         verify=null;
      }
   }
   
   public void invalid()
   {
      facesMessages.add("Please try again");
   }
   
   public boolean isRegistered()
   {
      return registered;
   }

   public String getVerify()
   {
      return verify;
   }

   public void setVerify(String verify)
   {
      this.verify = verify;
   }
   
   @Destroy @Remove
   public void destroy() {}
}
