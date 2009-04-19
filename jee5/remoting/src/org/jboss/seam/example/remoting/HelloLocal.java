package org.jboss.seam.example.remoting;

import javax.ejb.Local;
import org.jboss.seam.annotations.remoting.WebRemote;

@Local
public interface HelloLocal {
  @WebRemote
  public String sayHello(String name);
}

