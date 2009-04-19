package org.jboss.seam.example.remoting.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * This is a GWT service, which will be implemented as a Seam component on the server
 * (see the server package). GWT uses strongly typed RPC interfaces.
 * 
 * @author Michael Neale
 */
public interface MyService extends RemoteService
{
      public String askIt(String question);
      
}
