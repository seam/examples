package org.jboss.seam.example.remoting.chatroom;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;

@MessageDriven(activationConfig={
      @ActivationConfigProperty(propertyName="destinationType", propertyValue="javax.jms.Topic"),
      @ActivationConfigProperty(propertyName="destination", propertyValue="topic/chatroomTopic")
   })
@Name("logger")
public class LoggerBean implements MessageListener
{
   
   @Logger Log log;

   public void onMessage(Message msg)
   {
      try
      {
         ChatroomEvent event = (ChatroomEvent) ( (ObjectMessage) msg ).getObject();
         log.info( "#0: #1", event.getUser(), event.getData()==null ? event.getAction() : event.getData() );
      }
      catch (JMSException jmse)
      {
         throw new RuntimeException(jmse);
      }
   }

}
