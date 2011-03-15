package org.jboss.seam.examples.booking.exceptioncontrol;

import org.jboss.logging.Logger;
import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

/**
 * Logs all exceptions and allows the to propagate
 * 
 * @author <a href="http://community.jboss.org/people/spinner">Jose Freitas</a>
 */
@HandlesExceptions
public class GeneralExceptionHandler {
	
	public void printExceptionMessage(@Handles CaughtException<Throwable> event, Logger log) {
		log.info("Something bad happened: " + event.getException().getMessage());
		event.rethrow();
	}

}
