package org.jboss.seam.examples.booking;

import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.faces.ManagedBean;
//import javax.faces.RequestScoped;
import javax.inject.manager.Manager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

//@ManagedBean
//@RequestScoped
public class BeanLookup {
   public void lookupManager() {
      try {
         InitialContext ic = new InitialContext();
         Manager manager = (Manager) ic.lookup("java:comp/env/jcdi/Manager");
         if (manager != null) {
            Logger logger = Logger.getLogger(BeanLookup.class.getName());
            logger.log(Level.INFO, "JCDI manager: " + manager.toString());
            logger.log(Level.INFO, "helloWorld bean: " + String.valueOf(manager.getInstanceByName("helloWorld")));
         }
      } catch (NamingException ex) {
         Logger.getLogger(BeanLookup.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
}
