package org.jboss.seam.examples.booking.log;

import org.jboss.logging.Logger.Level;
import org.jboss.seam.solder.logging.LogMessage;
import org.jboss.seam.solder.logging.Message;
import org.jboss.seam.solder.logging.MessageLogger;

@MessageLogger
public interface BookingLog {
    @LogMessage(level = Level.INFO)
    @Message("%s selected the %s in %s.")
    void hotelSelected(String customerName, String hotelName, String city);
    
    @LogMessage(level = Level.INFO)
    @Message("%s initiated a booking at the %s.")
    void bookingInitiated(String customerName, String hotelName);
    
    // QUESTION can positional parameters be used in message string?
    @LogMessage(level = Level.INFO)
    @Message("New booking at the %s confirmed for %s.")
    void bookingConfirmed(String hotelName, String customerName);
    
    @LogMessage(level = Level.INFO)
    @Message("Does the persistence context still contain the hotel instance? %s")
    void hotelEntityInPersistenceContext(boolean state);
}
