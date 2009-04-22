package org.jboss.webbeans.resources;

import java.util.Hashtable;
import javax.inject.manager.Manager;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.jboss.webbeans.CurrentManager;

/**
 * A JNDI object factory which will return the current {@link javax.inject.manager.Manager}
 * when the JNDI name under which this factory is registered gets resolved.
 *
 * @author Dan Allen
 */
public class ManagerReferenceFactory extends Reference implements ObjectFactory
{
   public ManagerReferenceFactory()
   {
      super(Manager.class.getName(), ManagerReferenceFactory.class.getName(), null);
   }

   /**
    * Called by the JNDI container when the JNDI name under which this factory is registered gets resolved.
    *
    * @param ref the Reference
    * @param name not used
    * @param ctx not used
    * @param env not used
    *
    * @return The current JCDI root manager instance
    */
   public Object getObjectInstance(Object ref, Name name, Context ctx, Hashtable<?, ?> env) throws Exception {
      return CurrentManager.rootManager().getCurrent();
   }
}
