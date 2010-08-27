package org.jboss.seam.examples.booking.inventory;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.support.MavenArtifactResolver;
import org.jboss.seam.examples.booking.support.NoOpLogger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HotelSearchTest
{
   @Deployment
   public static Archive<?> createTestArchive() {
      // JavaArchive doesn't work on GlassFish (would work on JBoss AS, but it breaks because of the EJBs)
//      JavaArchive jar = ShrinkWrap.create("test.jar", JavaArchive.class)
//            .addPackage(HotelSearch.class.getPackage())
//            .addPackage(Hotel.class.getPackage())
//            .addManifestResource("META-INF/persistence.xml", "persistence.xml")
//            .addManifestResource(new ByteArrayAsset(new byte[0]), "beans.xml");
//      return jar;
      // WebArchive does work in all cases (except JBoss AS still breaks with EJBs)
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test")
         .addPackage(HotelSearch.class.getPackage())
         .addPackage(Hotel.class.getPackage())
         .addClasses(NoOpLogger.class)
         .addLibraries(
               MavenArtifactResolver.resolve("joda-time:joda-time:1.6"),
               MavenArtifactResolver.resolve("org.jboss.seam.international:seam-international-api:3.0.0.Alpha1"),
               MavenArtifactResolver.resolve("org.jboss.seam.international:seam-international:3.0.0.Alpha1")
         )
         .addWebResource("META-INF/persistence.xml", "classes/META-INF/persistence.xml")
         .addWebResource(new ByteArrayAsset(new byte[0]), "beans.xml");
      return war;
   }

   @Inject UserTransaction utx;
   @PersistenceContext EntityManager em;
   @Inject SearchCriteria criteria;
   @Inject HotelSearch hotelSearch;
   @Inject Instance<List<Hotel>> hotelsInstance;

   public void prepareSeedData() throws Exception
   {
      utx.begin();
      em.joinTransaction();
      em.createQuery("delete from Hotel").executeUpdate();
      em.persist(new Hotel("Doubletree Atlanta-Buckhead", "3342 Peachtree Road NE", "Atlanta", "GA", "30326", "USA"));
      utx.commit();
   }

   @Test
   public void testSearch() throws Exception
   {
      prepareSeedData();

      criteria.setQuery("atlanta");
      hotelSearch.find();
      List<Hotel> hotels = hotelsInstance.get();
      Assert.assertEquals(1, hotels.size());
      Assert.assertEquals(hotels.get(0).getName(), "Doubletree Atlanta-Buckhead");

      criteria.setQuery("boston");
      hotelSearch.find();
      hotels = hotelsInstance.get();
      Assert.assertEquals(0, hotels.size());
   }

}
