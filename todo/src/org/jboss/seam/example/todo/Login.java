package org.jboss.seam.example.todo;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.bpm.Actor;

@Name("login")
public class Login 
{
   
   @In private Actor actor;
   
   private String user;

   public String getUser() {
      return user;
   }

   public void setUser(String user) {
      this.user = user;
   }
   
   public String login()
   {
      actor.setId(user);
      return "/todo.jsp";
   }
}
