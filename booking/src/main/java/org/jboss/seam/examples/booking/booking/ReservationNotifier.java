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
package org.jboss.seam.examples.booking.booking;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.jboss.solder.logging.Logger;
import org.jboss.seam.examples.booking.model.Booking;

//@MessageDriven(activationConfig = {
//      @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/BookingTopic"),
//      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
//})
public class ReservationNotifier implements MessageListener {
    @Inject
    private Logger log;

    public void onMessage(Message message) {
        try {
            Booking booking = (Booking) ((ObjectMessage) message).getObject();
            log.info("In a real-world application, send e-mail containing reservation information to "
                    + booking.getUser().getEmailWithName());
        } catch (JMSException ex) {
            log.error("Error reading booking from topic");
        }
    }

}
