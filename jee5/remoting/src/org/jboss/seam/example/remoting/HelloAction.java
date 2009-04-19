package org.jboss.seam.example.remoting;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.Name;

@Stateless
@Name("helloAction")
public class HelloAction implements HelloLocal {
  public String sayHello(String name) {
    return "Hello, " + name;
  }
}