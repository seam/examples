//$Id$
package org.jboss.seam.example.jpa;

import static org.jboss.seam.ScopeType.EVENT;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Scope(EVENT)
@Name("changePassword")
public class ChangePasswordAction
{

   @In @Out
   private User user;
   
   @In
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
   
}
