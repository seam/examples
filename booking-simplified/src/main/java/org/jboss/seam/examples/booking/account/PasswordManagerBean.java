package org.jboss.seam.examples.booking.account;

import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
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
@Named("passwordManager")
@Stateful
@RequestScoped
public class PasswordManagerBean implements PasswordManager
{
   @PersistenceContext
   private EntityManager em;

   @Inject
   private Messages messages;

   @Inject
   private RegistrationFormControls formControls;

   @Inject
   @Registered
   private User user;

   private String confirmPassword;

   private boolean changed;

   public void changePassword()
   {
      if (user.getPassword().equals(confirmPassword))
      {
         // FIXME: dirty hack, can't merge a managed bean
         em.merge(new User(user.getName(), user.getUsername(), user.getPassword()));
         user.setPassword(null);
         messages.info(new BundleKey("messages.properties", "account.passwordChanged")).textDefault("Password successfully updated.");
         changed = true;
      }
      else
      {
         // FIME reverting isn't going to work here
         // revertUser();
         confirmPassword = null;
         messages.error(new BundleKey("messages.properties", "account.passwordsDoNotMatch")).textDefault("Passwords do not match. Please re-type the new password.");
      }
   }

   public boolean isChanged()
   {
      return changed;
   }

   public void setConfirmPassword(final String password)
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
