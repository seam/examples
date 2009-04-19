package org.jboss.seam.example.remoting.gwt.server;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.example.remoting.gwt.client.MyService;
import org.jboss.seam.example.remoting.gwt.client.ValidationUtility;

/**
 * This is the seam component that implements the service the GUI uses.
 * Note that @WebRemote is needed, as is @Name to match the service full class name
 * (defaults are not a great idea, as this is open to the wide world !)
 * 
 * @author Michael Neale
 */
@Name("org.jboss.seam.example.remoting.gwt.client.MyService")
public class ServiceImpl implements MyService
{

   @WebRemote
   public String askIt(String question)
   {
      if (!validate(question)) {
         throw new IllegalStateException("Hey, this shouldn't happen, I checked on the client, " +
               "but its always good to double check.");
      }
      return "42. Its the real question that you seek now.";
   }
   
   /**
    * Woh, we can re-use code from the client !
    */
   public boolean validate(String q) {
      ValidationUtility util = new ValidationUtility();
      return util.isValid(q);
   }

}
