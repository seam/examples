package org.jboss.seam.example.guice;

import com.google.inject.Module;
import com.google.inject.Binder;

/**
 * @author Pawel Wrzeszcz (pwrzeszcz [at] jboss . org)
 */
public class JuiceBarModule implements Module
{
   public void configure(Binder binder)
   {
      binder.bind(Juice.class).toInstance(new JuiceImpl("Apple Juice", 10));
      binder.bind(Juice.class).annotatedWith(Orange.class).toInstance(new JuiceImpl("Orange Juice", 12)); 
   }
}
