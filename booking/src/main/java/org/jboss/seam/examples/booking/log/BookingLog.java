/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.examples.booking.log;

import org.jboss.logging.Logger.Level;
import org.jboss.seam.solder.logging.Log;
import org.jboss.seam.solder.logging.MessageLogger;
import org.jboss.seam.solder.messages.Message;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@MessageLogger
public interface BookingLog {
    @Log(level = Level.INFO)
    @Message("%s selected the %s in %s.")
    void hotelSelected(String customerName, String hotelName, String city);

    @Log(level = Level.INFO)
    @Message("%s initiated a booking at the %s.")
    void bookingInitiated(String customerName, String hotelName);

    // QUESTION can positional parameters be used in message string?
    @Log(level = Level.INFO)
    @Message("New booking at the %s confirmed for %s.")
    void bookingConfirmed(String hotelName, String customerName);

    @Log(level = Level.INFO)
    @Message("Does the persistence context still contain the hotel instance? %s")
    void hotelEntityInPersistenceContext(boolean state);
}
