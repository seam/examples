package org.jboss.seam.examples.booking.account;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.controls.RegistrationFormControls;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;

/**
 * @author Dan Allen
 */
@Stateful
@Named("registrar")
@RequestScoped
public class RegistrarBean implements Registrar
{
   @PersistenceContext
   private EntityManager em;

   @Inject
   private Messages messages;

   @Inject
   private RegistrationFormControls formControls;

   @Inject
   @Registered
   User newUser;

   private String confirmPassword;

   private boolean registered;

   private boolean registrationInvalid;

   public void register()
   {
      if (verifyPasswordsMatch() && verifyUsernameIsAvailable())
      {
         registered = true;
         messages.info(new BundleKey("messages.properties", "registration.registered")).textDefault("You have been successfully registered as the user {0}!").textParams(newUser.getUsername());
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

   // TODO it would be nice to move the conditional to the UI but <f:event>
   // doesn't support if=""
   public void notifyIfRegistrationIsInvalid(final boolean validationFailed)
   {
      if (validationFailed || registrationInvalid)
      {
         messages.warn(new BundleKey("messages.properties", "registration.invalid")).textDefault("Invalid registration. Please correct the errors and try again.");
      }
   }

   @Produces
   @Named
   @RequestScoped
   public User getNewUser()
   {
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

   public void setConfirmPassword(final String password)
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
         messages.warn(new BundleKey("messages.properties", "account.passwordsDoNotMatch")).textDefault("Passwords do not match. Please re-type your password.").targets(formControls.getConfirmPasswordControlId());
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
         messages.warn(new BundleKey("messages.properties", "account.usernameTaken")).textDefault("The username '{0}' is already taken. Please choose another username.").targets(formControls.getUsernameControlId()).textParams(newUser.getUsername());
         return false;
      }

      return true;
   }

   @PreDestroy
   public void destroy()
   {
   }
}
