package org.jboss.seam.example.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;

@Name("buttonLink")
public class ButtonLinkBean
{
   private String foo;
   
   public void simpleAction()
   {
      if (foo != null)
      {
         FacesMessages.instance().add("Foo = " + foo);
      }
      else
      {
         FacesMessages.instance().add("A simple action was performed");
      }
   }
   
   public void setFoo(String foo)
   {
      this.foo = foo;
   }
   
   public String getFoo()
   {
      return foo;
   }
   
}
