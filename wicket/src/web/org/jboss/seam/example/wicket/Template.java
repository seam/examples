package org.jboss.seam.example.wicket;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.Link;
import org.jboss.seam.annotations.In;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.wicket.action.User;
import org.jboss.seam.security.Identity;

public class Template extends Border
{

   @In 
   private User user;
   
   @In
   private Identity identity;
   
   public Template(String id)
   {
      super(id);
      add(new Link("search")
      {
         @Override
         public void onClick()
         {
            Manager.instance().leaveConversation();
            setResponsePage(Main.class);
         }
      });
      add(new Link("settings")
      {
         @Override
         public void onClick()
         {
            Manager.instance().leaveConversation();
            setResponsePage(Password.class);
         }
      });
      add(new Link("logout")
      {
         @Override
         public void onClick()
         {
            identity.logout();
            setResponsePage(Home.class);
         }
      });
      add(new Label("userName", user.getName()));
   }

}
