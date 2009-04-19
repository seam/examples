//$Id: ChangePasswordAction.java,v 1.1 2007/06/23 18:33:59 pmuir Exp $
package org.jboss.seam.example.booking;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;

@Stateful
@Scope(EVENT)
@Name("changePassword")
@Restrict("#{identity.loggedIn}")
public class ChangePasswordAction implements ChangePassword, Serializable
{

   @In @Out
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   private String verify;
   
   private boolean changed;
   
   public void changePassword()
   {
      if ( user.getPassword().equals(verify) )
      {
         user = em.merge(user);
         FacesMessages.instance().add("Password updated");
         changed = true;
      }
      else 
      {
         FacesMessages.instance().add("verify", "Re-enter new password");
         revertUser();
         verify=null;
      }
   }
   
   public boolean isChanged()
   {
      return changed;
   }
   
   private void revertUser()
   {
      user = em.find(User.class, user.getUsername());
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
