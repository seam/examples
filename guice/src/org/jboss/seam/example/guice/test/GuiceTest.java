package org.jboss.seam.example.guice.test;

import org.jboss.seam.example.guice.JuiceBar;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.ioc.guice.Injector;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author Pawel Wrzeszcz (pwrzeszcz [at] jboss . org)
 */
public class GuiceTest extends SeamTest
{
   private static final String[] expectedModules = { "org.jboss.seam.example.guice.JuiceBarModule" };

   @Test
   public void testGuice()
   {
      new ComponentTest()
      {
         protected void testComponents() throws Exception
         {
            Injector injector = (Injector) getValue("guiceExampleInjector");
            assert Arrays.equals(expectedModules, injector.getModules());

            JuiceBar juiceBar = (JuiceBar) getValue("juiceBar");
            assert "Apple Juice".equals(juiceBar.getJuiceOfTheDay().getName());
            assert "Orange Juice".equals(juiceBar.getAnotherJuice().getName());             
         }
      };
   }
}
