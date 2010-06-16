package org.jboss.seam.examples.booking.account;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
   @Authenticated
   private User user;

   private String confirmPassword;

   private boolean changed;

   public void changePassword()
   {
      em.merge(user);
      messages.info(new BundleKey("messages", "account.passwordChanged")).textDefault("Password successfully updated.");
      changed = true;
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
}
