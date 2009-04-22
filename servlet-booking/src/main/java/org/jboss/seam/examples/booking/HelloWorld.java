package org.jboss.seam.examples.booking;

import javax.annotation.Named;
import javax.inject.Current;
import javax.inject.manager.Manager;
import javax.context.RequestScoped;

@Named
@RequestScoped
public class HelloWorld {

   private @Current Manager manager;

   public void sayHello() {
       System.out.println("Hello! Here is the manager that I found: " + manager);
   }

}
