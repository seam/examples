package org.jboss.seam.example.remoting.chatroom;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Set;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("chatroomAction")
@Scope(CONVERSATION)
public class ChatRoomAction implements ChatRoomActionWebRemote
{

   @In(create=true)
   private transient TopicPublisher topicPublisher;   
   @In(create=true)
   private transient TopicSession topicSession;

   @In(create=true)
   Set<String> chatroomUsers;
   
   private String username;
   
   @Begin
   public boolean connect(String username)
   {
      this.username = username;
      boolean added = chatroomUsers.add(username);
      if (added)
      {
         publish( new ChatroomEvent("connect", username) );
      }
      return added;
   }

   public void sendMessage(String message)
   {
      publish( new ChatroomEvent("message", username, message) );
   }

   @End
   public void disconnect()
   {
      chatroomUsers.remove(username);
      publish( new ChatroomEvent("disconnect", username) );
   }

   public Set<String> listUsers()
   {
      return chatroomUsers;
   }

   private void publish(ChatroomEvent message)
   {
      try
      {
         topicPublisher.publish( topicSession.createObjectMessage(message) );
      } 
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      } 
   }
   
   @Destroy
   @Remove
   public void destroy() {}

}
