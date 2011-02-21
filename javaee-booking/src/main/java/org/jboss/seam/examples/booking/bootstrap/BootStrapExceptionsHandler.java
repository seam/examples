package org.jboss.seam.examples.booking.bootstrap;

import javax.faces.validator.ValidatorException;

import org.jboss.seam.exception.control.CaughtException;
import org.jboss.seam.exception.control.Handles;
import org.jboss.seam.exception.control.HandlesExceptions;

@HandlesExceptions
public class BootStrapExceptionsHandler {

	public void printExceptions(@Handles CaughtException<Throwable> event) {

		System.out.println("**************************** \n \n \n \n Something bad happened: " + event.getException().getMessage());
		event.rethrow();

	}

	public void catchValidatorException(@Handles CaughtException<ValidatorException> event) {

		System.out.println("**************************** \n \n \n \n Something bad happened: " + event.getException().getMessage());
		event.rethrow();

	}

}
