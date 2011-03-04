package org.jboss.seam.examples.booking.exceptioncontrol;

import javax.inject.Inject;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;
import org.jboss.logging.Logger;

/**
 * Logs all exceptions and allows the to propogate
 * 
 * @author <a href="http://community.jboss.org/people/spinner)">jose.freitas</a>
 */
@HandlesExceptions
public class GeneralExceptionHandler {
	@Inject
	private Logger log;
	
	public void printExceptionMessage(@Handles CaughtException<Throwable> event) {
		log.info("Something bad happened: " + event.getException().getMessage());
		event.rethrow();
	}

}
