package org.jboss.seam.example.restbay;

import javax.ws.rs.Path;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.framework.Home;
import org.jboss.seam.resteasy.ResourceHome;

/**
 * This resource demonstrates using ResourceHome component. This resource is
 * used for testing purposes.
 * 
 * @author Jozef Hartinger
 * 
 */

@Name("categoryResourceHome")
@Path("extendedCategory")
public class CategoryResourceHome extends ResourceHome<Category, Integer>
{

   @In
   private EntityHome<Category> categoryHome;

   @Override
   public Home<?, Category> getEntityHome()
   {
      return categoryHome;
   }

   public CategoryResourceHome()
   {
      setMediaTypes(new String[] { "application/xml", "application/json" });
   }
}
