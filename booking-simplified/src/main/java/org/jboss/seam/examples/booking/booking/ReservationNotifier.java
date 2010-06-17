package org.jboss.seam.examples.booking.booking;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import org.jboss.seam.examples.booking.model.Booking;
import org.slf4j.Logger;

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