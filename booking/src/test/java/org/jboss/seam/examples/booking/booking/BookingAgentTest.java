/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.examples.booking.booking;

import java.util.HashMap;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.log.BookingLog;
import org.jboss.seam.examples.booking.model.Booking;
import org.jboss.seam.examples.booking.model.CreditCardType;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.examples.booking.support.MavenArtifactResolver;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.Container;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequest;
import org.jboss.weld.context.bound.MutableBoundRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BookingAgentTest {
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackage(Hotel.class.getPackage())
                .addClasses(BookingAgent.class, BookingAgent.class, Confirmed.class, Authenticated.class, DefaultBundleKey.class, AuthenticatedUserProducer.class)
                .addPackage(BookingLog.class.getPackage())
                .addAsLibraries(
                        MavenArtifactResolver.resolve("joda-time:joda-time:1.6"),
                        MavenArtifactResolver.resolve("org.jboss.seam.solder:seam-solder:3.0.0.CR4"),
                        MavenArtifactResolver.resolve("org.jboss.seam.international:seam-international:3.0.0.CR4"))
                .addAsWebInfResource("test-persistence.xml", "classes/META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    UserTransaction utx;

    @PersistenceContext
    EntityManager em;

    @Inject
    BookingAgent bookingAgent;

    @Inject
    Instance<Booking> bookingInstance;

    public void prepareSeedData() throws Exception {
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
    public void testBookHotel() throws Exception {
        prepareSeedData();

        BoundConversationContext ctx = null;
        BoundRequest storage = new MutableBoundRequest(new HashMap<String, Object>(), new HashMap<String, Object>());
        try {
            ctx = Container.instance().deploymentManager().instance().select(BoundConversationContext.class).get();
            ctx.associate(storage);
            ctx.activate();

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
        finally {
            if (ctx != null && ctx.isActive()) {
                ctx.deactivate();
                ctx.dissociate(storage);
            }
        }
    }
}
