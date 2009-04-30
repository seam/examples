package org.jboss.seam.examples.booking.session;

import javax.ejb.Local;

/**
 *
 * @author Dan Allen
 */
public
@Local
interface PasswordManager {
   void changePassword();
   boolean isChanged();
   void setConfirmPassword(String password);
   String getConfirmPassword();
   void destroy();
}
