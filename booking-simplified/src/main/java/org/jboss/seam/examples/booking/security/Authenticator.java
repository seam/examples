package org.jboss.seam.examples.booking.security;

import javax.ejb.Local;

/**
 * <strong>Authenticator</strong> is responsible for authenticating the current
 * user. The sole method of this interface, authenticate(), is invoked as a
 * callback method to Seam's security infrastructure during authentication.
 * 
 * @author Dan Allen
 */
@Local
public interface Authenticator // extends org.jboss.seam.security.Authenticator
{
   public boolean authenticate();
}
