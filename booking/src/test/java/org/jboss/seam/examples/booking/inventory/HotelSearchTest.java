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
package org.jboss.seam.examples.booking.inventory;

import java.util.HashMap;
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
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class HotelSearchTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addPackage(HotelSearch.class.getPackage())
                .addPackage(Hotel.class.getPackage())
                .addAsLibraries(
                        MavenArtifactResolver.resolve("joda-time:joda-time:1.6"),
                        MavenArtifactResolver.resolve("org.jboss.seam.solder:seam-solder:3.0.0.CR4"),
                        MavenArtifactResolver.resolve("org.jboss.seam.international:seam-international:3.0.0.CR4"))
                .addAsWebInfResource("test-persistence.xml", "classes/META-INF/persistence.xml")
                .addAsWebInfResource(new StringAsset(""), "beans.xml");
        return war;
    }

    @Inject
    UserTransaction utx;
    
    @PersistenceContext
    EntityManager em;
    
    @Inject
    SearchCriteria criteria;
    
    @Inject
    HotelSearch hotelSearch;
    
    @Inject
    Instance<List<Hotel>> hotelsInstance;

    public void prepareSeedData() throws Exception {
        utx.begin();
        em.joinTransaction();
        em.createQuery("delete from Hotel").executeUpdate();
        em.persist(new Hotel("Doubletree Atlanta-Buckhead", "3342 Peachtree Road NE", "Atlanta", "GA", "30326", "USA"));
        utx.commit();
    }

    @Test
    public void testSearch() throws Exception {
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
