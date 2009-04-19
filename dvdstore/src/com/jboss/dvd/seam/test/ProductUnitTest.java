package com.jboss.dvd.seam.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

import com.jboss.dvd.seam.Product;

public class ProductUnitTest 
    extends SeamTest
{   
    @Test
    public void testRequiredAttributes()
        throws Exception
    {
        new ComponentTest() {

            @Override
            protected void testComponents()
                throws Exception 
            {
                Product p = new Product();

                EntityManager em = (EntityManager) getValue("#{entityManager}");
                try {
                    em.persist(p);
                    fail("empty product persisted");
                } catch (PersistenceException e) {
                    // good
                }                 
            }            
        }.run();
    }

     @Test 
     public void testCreateDelete() 
         throws Exception 
     {
         final Product p = new Product();
         p.setTitle("test");

         new FacesRequest() {
            protected void invokeApplication()
            {
                EntityManager em = (EntityManager) getValue("#{entityManager}");                
                em.persist(p);            
            }
            
           
         }.run();
         
         new FacesRequest() {
             protected void invokeApplication()
             { 
                 EntityManager em = (EntityManager) getValue("#{entityManager}");
                 Product found = em.find(Product.class ,p.getProductId());
                 assertNotNull("find by id", found);
                 assertEquals("id", p.getProductId(), found.getProductId());
                 assertEquals("title", "test", found.getTitle());
         
                 em.remove(found);             
             }
          }.run();
         
          new FacesRequest() {
              protected void invokeApplication()
              { 
                  EntityManager em = (EntityManager) getValue("#{entityManager}");
                  Product found = em.find(Product.class ,p.getProductId());

                  assertNull("deleted product", found);             
              }
           }.run();
          
 
     }
    
}
