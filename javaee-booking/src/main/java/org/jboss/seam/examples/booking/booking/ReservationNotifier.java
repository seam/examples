/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.examples.booking.booking;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.jboss.logging.Logger;
import org.jboss.seam.examples.booking.model.Booking;

//@MessageDriven(activationConfig = {
//      @ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/BookingTopic"),
//      @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
//})
public class ReservationNotifier implements MessageListener
{
	@Inject
	private Logger log;

	public void onMessage(Message message)
	{
		try
		{
			Booking booking = (Booking) ((ObjectMessage) message).getObject();
			log.info("In a real-world application, send e-mail containing reservation information to " + booking.getUser().getEmailWithName());
		} catch (JMSException ex)
		{
			log.error("Error reading booking from topic");
		}
	}

}
