package org.jboss.seam.examples.booking.account;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Current;
import javax.enterprise.inject.Named;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.controls.RegistrationFormControls;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

/**
 * @author Dan Allen
 */
public
@Named("registrar")
@Stateful
@RequestScoped
class RegistrarBean implements Registrar
{
   @PersistenceContext EntityManager em;

   @Current StatusMessages statusMessages;

   @Current RegistrationFormControls formControls;

   @Current Credentials credentials;

   @Current Identity identity;

   private User newUser;

   private String confirmPassword;

   private boolean registered;

   private boolean registrationInvalid;

   public void register()
   {
      if (verifyPasswordsMatch() && verifyUsernameIsAvailable())
      {
         em.persist(newUser);
         credentials.setUsername(newUser.getUsername());
         identity.login();
         registered = true;
         statusMessages.add("You have been successfully registered as the user {0}!", newUser.getUsername());
      }
      else
      {
         registrationInvalid = true;
      }
   }

   public boolean isRegistrationInvalid()
   {
      return registrationInvalid;
   }

   // TODO it would be nice to move the conditional to the UI but <f:event> doesn't support if=""
   public void notifyIfRegistrationIsInvalid(boolean validationFailed)
   {
      if (validationFailed || registrationInvalid)
      {
         statusMessages.add(StatusMessage.Severity.WARN, "Invalid registration. Please correct the errors and try again.");
      }
   }

   public
   @Produces
   @Named
   @RequestScoped
   User getNewUser()
   {
      newUser = new User();
      return newUser;
   }

   public boolean isRegistered()
   {
      return registered;
   }

   public String getConfirmPassword()
   {
      return confirmPassword;
   }

   public void setConfirmPassword(String password)
   {
      this.confirmPassword = password;
   }

   /**
    * Verify that the same password is entered twice.
    */
   private boolean verifyPasswordsMatch()
   {
      if (!newUser.getPassword().equals(confirmPassword))
      {
         statusMessages.addToControl(formControls.getConfirmPasswordControlId(), "Passwords do not match. Please re-type your password.");
         confirmPassword = null;
         return false;
      }

      return true;
   }

   private boolean verifyUsernameIsAvailable()
   {
      User existing = em.find(User.class, newUser.getUsername());
      if (existing != null)
      {
         statusMessages.addToControl(formControls.getUsernameControlId(), "The username '{0}' is already taken. Please choose another username.", newUser.getUsername());
         return false;
      }

      return true;
   }

   @PreDestroy
   public void destroy()
   {
   }
}
