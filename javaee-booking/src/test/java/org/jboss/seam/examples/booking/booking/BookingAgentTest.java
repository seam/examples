package org.jboss.seam.examples.booking.booking;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.CreditCardType;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.examples.booking.support.MavenArtifactResolver;
import org.jboss.seam.examples.booking.support.NoOpLogger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.api.ContextualInstance;
import org.jboss.weld.context.api.helpers.AbstractMapBackedBeanStore;
import org.jboss.weld.manager.BeanManagerImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BookingAgentTest
{
   @Deployment
   public static Archive<?> createTestArchive()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackage(Hotel.class.getPackage())
            .addClasses(BookingAgent.class, BookingAgent.class, Confirmed.class,
                  Authenticated.class, NoOpLogger.class)
            .addLibraries(
                  MavenArtifactResolver.resolve("joda-time:joda-time:1.6"),
                  MavenArtifactResolver.resolve("org.jboss.seam.international:seam-international-api:3.0.0.Alpha1"),
                  MavenArtifactResolver.resolve("org.jboss.seam.international:seam-international:3.0.0.Alpha1"))
            .addWebResource("test-persistence.xml", "classes/META-INF/persistence.xml")
            .addWebResource(new StringAsset(""), "beans.xml");
      return war;
   }

   @Inject
   BeanManager beanManager;
   
   @Inject
   UserTransaction utx;

   @PersistenceContext
   EntityManager em;

   @Inject
   BookingAgent bookingAgent;

   @Inject
   Instance<Booking> bookingInstance;

   public void prepareSeedData() throws Exception
   {
      utx.begin();
      em.joinTransaction();
      em.createQuery("delete from Booking").executeUpdate();
      em.createQuery("delete from Hotel").executeUpdate();
      em.persist(new Hotel("Doubletree Atlanta-Buckhead", "3342 Peachtree Road NE", "Atlanta", "GA", "30326", "USA"));
      em.createQuery("delete from User").executeUpdate();
      em.persist(new User("Ike", "ike", "ike@mailinator.com", "secret"));
      utx.commit();
   }

   @Test
   public void testBookHotel() throws Exception
   {
      prepareSeedData();
      
      // we have to depend on the Weld API to setup the conversation scope
      ConversationContext cc = new ConversationContext();
      cc.setBeanStore(new HashMapBeanStore());
      cc.setActive(true);
      ((BeanManagerImpl) beanManager).addContext(cc);

      bookingAgent.selectHotel(1l);
      bookingAgent.bookHotel();
      Booking booking = bookingInstance.get();
      booking.setCreditCardNumber("1111222233334444");
      booking.setCreditCardExpiryMonth(1);
      booking.setCreditCardExpiryYear(2012);
      booking.setCreditCardType(CreditCardType.VISA);
      booking.setBeds(1);
      booking.setSmoking(false);
      bookingAgent.confirm();

      Assert.assertEquals(1, em.createQuery("select b from Booking b").getResultList().size());
   }

   @Produces
   @Authenticated
   User getRegisteredUser()
   {
      return em.find(User.class, "ike");
   }

   public static class HashMapBeanStore extends AbstractMapBackedBeanStore implements Serializable
   {
      protected Map<String, ContextualInstance<? extends Object>> delegate;

      public HashMapBeanStore()
      {
         delegate = new HashMap<String, ContextualInstance<? extends Object>>();
      }

      @Override
      protected Map<String, ContextualInstance<? extends Object>> delegate()
      {
         return delegate;
      }
   }

}
