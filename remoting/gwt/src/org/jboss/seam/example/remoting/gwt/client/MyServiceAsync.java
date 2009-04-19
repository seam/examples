package org.jboss.seam.example.remoting.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * This is the interface the client code uses. You NEVER implement this directly, 
 * GWT does this for you, and calls are marshalled through to the Sync equivalent method in MyService
 * on the server (which is a Seam component).
 * 
 * @author Michael Neale
 */
public interface MyServiceAsync extends RemoteService 
{
   public void askIt(String question, AsyncCallback callback);
}
