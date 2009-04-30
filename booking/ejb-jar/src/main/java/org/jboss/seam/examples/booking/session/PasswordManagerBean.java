package org.jboss.seam.examples.booking.session;

import javax.annotation.PreDestroy;
import javax.context.RequestScoped;
import javax.ejb.Stateful;
import javax.inject.Current;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.Registered;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.StatusMessages;
import ui.RegistrationFormControls;

/**
 *
 * @author Dan Allen
 */
public
@Stateful
@RequestScoped
class PasswordManagerBean implements PasswordManager
{
   private @PersistenceContext EntityManager em;

   private @Current StatusMessages statusMessages;

   private @Current RegistrationFormControls formControls;

   private @Registered User user;

   private String confirmPassword;

   private boolean changed;

   public void changePassword()
   {
      if (user.getPassword().equals(confirmPassword))
      {
         user = em.merge(user);
         statusMessages.add("Password successfully updated.");
         changed = true;
      }
      else
      {
         // FIME reverting isn't going to work here
         //revertUser();
         confirmPassword = null;
         statusMessages.addToControl(formControls.getConfirmPasswordControlId(), "Passwords do not match. Please re-type the new password.");
      }
   }

   public boolean isChanged()
   {
      return changed;
   }

   public void setConfirmPassword(String password)
   {
      this.confirmPassword = password;
   }

   public String getConfirmPassword()
   {
      return this.confirmPassword;
   }

   @PreDestroy
   public void destroy()
   {
   }
}
