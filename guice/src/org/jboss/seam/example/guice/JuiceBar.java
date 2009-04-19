package org.jboss.seam.example.guice;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.ioc.guice.Guice;
import com.google.inject.Inject;

/**
 * @author Pawel Wrzeszcz (pwrzeszcz [at] jboss . org)
 */
@Name("juiceBar")
@Guice // Activates @Inject on a Seam component
public class JuiceBar
{
   @Inject private Juice juiceOfTheDay; // Guice looks at the variable type, not name
   @Inject @Orange private Juice anotherJuice;

   public Juice getJuiceOfTheDay()
   {
       return juiceOfTheDay;
   }

   public Juice getAnotherJuice()
   {
       return anotherJuice;
   }
}
