package org.jboss.seam.examples.booking;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Current;
import javax.enterprise.inject.Named;
import javax.enterprise.inject.spi.BeanManager;

@Named
@RequestScoped
public class HelloWorld {

   private @Current BeanManager manager;

   public void sayHello() {
       System.out.println("Hello! Here is the manager that I found: " + manager);
   }

}
