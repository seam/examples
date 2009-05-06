package org.jboss.seam.examples.booking.security;

import javax.ejb.Local;

/**
 * <strong>Authenticator</strong> is responsible for authenticating
 * the current user.
 *
 * @author Dan Allen
 */
public
@Local
interface Authenticator extends org.jboss.seam.security.Authenticator {
   
}
