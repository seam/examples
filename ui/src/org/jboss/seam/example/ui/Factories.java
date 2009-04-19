package org.jboss.seam.example.ui;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.example.ui.Person.Honorific;
import org.jboss.seam.example.ui.Person.Role;

@Name("factories")
public class Factories
{
   @Factory("honorifics")
   public Honorific[] getHonorifics() {
      return Honorific.values();
   }
   
   @Factory("roles")
   public Role[] getRoles() {
      return Role.values();
   }

}
