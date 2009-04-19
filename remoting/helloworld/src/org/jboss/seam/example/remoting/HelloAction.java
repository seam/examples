package org.jboss.seam.example.remoting;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;

@Name("helloAction")
public class HelloAction {
   @WebRemote
   public String sayHello(String name) {
      return "Hello, " + name;
   }
}