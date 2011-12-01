package org.jboss.seam.security.examples.authorization.exception;

import javax.inject.Inject;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;

/**
 * Handles user authorization exceptions
 *
 * @author Shane Bryzak
 */
@HandlesExceptions
public class ExceptionHandler {
    @Inject Messages messages;

    public void handleAuthorizationException(@Handles CaughtException<AuthorizationException> evt) {
        messages.error("You do not have the necessary permissions to perform that operation", "");
        evt.handled();
    }
}
