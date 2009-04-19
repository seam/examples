package org.jboss.seam.example.remoting.gwt.client;

/**
 * This simple validation utility shows how you can have the same code on the "client" 
 * as on the server (ie the server can re-use some code from the client - one of the GWT advantages).
 * 
 * @author michael
 */
public class ValidationUtility
{

   public boolean isValid(String question) {
      if ("".equals(question)) {
         return false;
      } else if (!question.trim().endsWith("?")) {
         return false;
      } else {
         return true;
      }
   }
   
}
