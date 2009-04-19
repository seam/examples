package org.jboss.seam.example.restbay;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;

import javax.ws.rs.*;
import javax.persistence.EntityManager;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("categoryService")
@Path("/category")
public class CategoryServiceImpl implements CategoryService
{

   @In
   EntityManager entityManager;

   @GET
   @Produces("text/plain")
   public String getCategories()
   {
      List<Object[]> categories =
            entityManager.createQuery("select c.categoryId, c.name from Category c order by c.id asc").getResultList();
      StringBuilder s = new StringBuilder();
      for (Object[] category : categories)
      {
         s.append(category[0]).append(",").append(category[1]).append("\n");
      }

      return s.toString();
   }

   @GET
   @Path("/{categoryId}")
   @Produces("text/plain")
   public String getCategory(@PathParam("categoryId") int categoryId)
   {
      return entityManager.find(Category.class, categoryId).getName();
   }

}