package org.jboss.seam.examples.booking.account;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.jboss.seam.examples.booking.model.User;

/**
 * @author Dan Allen
 */
@Local
public interface Registrar
{
   void register();

   boolean isRegistrationInvalid();

   void notifyIfRegistrationIsInvalid(boolean validationFailed);

   User getNewUser();

   boolean isRegistered();

   @NotNull
   @Size(min = 5, max = 15)
   String getConfirmPassword();

   void setConfirmPassword(String password);
}
