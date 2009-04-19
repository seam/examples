package org.jboss.seam.example.seambay;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.remoting.WebRemote;

@Name("categoryAction")
public class CategoryAction
{
   @In
   EntityManager entityManager;
   
   @Out(required = false)
   private List<Category> categories;
   
   @Out(required = false)
   private List<Category> allCategories;

   @Out(required = false)
   private List<Category> leftCategories;
   
   @Out(required = false)
   private List<Category> rightCategories;
   
   @SuppressWarnings("unchecked")
   @Factory("categories")
   public void loadCategories()
   {
      categories = entityManager.createQuery(
            "from Category where parent = null order by name")
            .getResultList();
   }
   
   public List<Category> getCategories()
   {
      return categories;
   }
   
   @SuppressWarnings("unchecked")
   @Factory("allCategories")
   @WebRemote
   public List<Category> getAllCategories()
   {
      allCategories = entityManager.createQuery("from Category").getResultList(); 
      return allCategories;
   }
   
   /**
    * On the "Buy" screen the list of categories are split into two columns,
    * the left column and the right column.  This procedure loads the categories
    * and separates them into two lists to populate these columns.
    */
   private void loadLeftAndRight()
   {
      if (categories == null) loadCategories();
      
      boolean loadLeft = leftCategories == null;
      boolean loadRight = rightCategories == null;

      if (loadLeft) leftCategories = new ArrayList<Category>();
      if (loadRight) rightCategories = new ArrayList<Category>();
      
      for (int i = 0; i < categories.size(); i++)
      {
         if (i <= (categories.size() / 2))
         {
            if (loadLeft) leftCategories.add(categories.get(i));
         }
         else if (loadRight)
         {
            rightCategories.add(categories.get(i));
         }
      }
   }
   
   @Factory("leftCategories")
   public void loadLeftCategories()
   {
      if (leftCategories == null) loadLeftAndRight();
   }
   
   @Factory("rightCategories")
   public void loadRightCategories()
   {
      if (rightCategories == null) loadLeftAndRight();
   }
   
   @SuppressWarnings("unchecked")
   public List<Category> getSubCategories(Category parent)
   {
      return entityManager.createQuery(
            "from Category where parent = :parent")
            .setParameter("parent", parent)
            .getResultList();
   }
}
