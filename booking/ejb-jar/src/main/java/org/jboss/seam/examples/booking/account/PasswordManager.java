package org.jboss.seam.examples.booking.account;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Dan Allen
 */
public
@Local
interface PasswordManager
{
   void changePassword();

   boolean isChanged();

   void setConfirmPassword(String password);

   @NotNull
   @Size(min = 5, max = 15)
   String getConfirmPassword();

   void destroy();
}
