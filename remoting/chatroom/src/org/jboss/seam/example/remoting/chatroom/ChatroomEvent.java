package org.jboss.seam.example.remoting.chatroom;

import java.io.Serializable;

import org.jboss.seam.annotations.Name;

/**
 * @author Shane Bryzak
 */
@Name("chatroomEvent")
public class ChatroomEvent implements Serializable
{
   private String action;
   private String user;
   private String data;

   public ChatroomEvent(String action, String user)
   {
      this(action, user, null);
   }
   
   public ChatroomEvent(String action, String user, String data)
   {
      this.action = action;
      this.user = user;
      this.data = data;
   }

   public String getAction()
   {
      return action;
   }

   public String getUser()
   {
      return user;
   }

   public String getData()
   {
      return data;
   }
}
