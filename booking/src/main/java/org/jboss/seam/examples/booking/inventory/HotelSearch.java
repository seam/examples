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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.logging.Logger;
import org.jboss.seam.examples.booking.model.Hotel;
import org.jboss.seam.examples.booking.model.Hotel_;
import org.jboss.seam.international.status.builder.TemplateMessage;
import org.jboss.seam.solder.core.ExtensionManaged;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 */
@Named
@Stateful
@SessionScoped
public class HotelSearch {

    @Inject
    private Logger log;

    @ExtensionManaged
    @Produces
    @PersistenceUnit
    @ConversationScoped
    static EntityManagerFactory producerField;

    @Inject
    private EntityManager entityManager;

    @Inject
    private SearchCriteria criteria;

    @Inject
    private Instance<TemplateMessage> messageBuilder;

    private boolean nextPageAvailable = false;

    private List<Hotel> hotels = new ArrayList<Hotel>();

    private boolean advancedSearch = false;

    private Hotel hotel = new Hotel();

    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAdvancedSearch(boolean advancedSearch) {
        this.advancedSearch = advancedSearch;
    }

    public boolean isAdvancedSearch() {
        return advancedSearch;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public void find() {
        criteria.firstPage();
        queryHotels(criteria);
    }

    public void nextPage() {
        criteria.nextPage();
        queryHotels(criteria);
    }

    public void previousPage() {
        criteria.previousPage();
        queryHotels(criteria);
    }

    @Produces
    @Named
    public List<Hotel> getHotels() {
        return hotels;
    }

    public boolean isNextPageAvailable() {
        return nextPageAvailable;
    }

    public boolean isPreviousPageAvailable() {
        return criteria.getPage() > 0;
    }

    private void queryHotels(final SearchCriteria criteria) {
        List<Hotel> results;

        if (isAdvancedSearch()) {

            results = entityManager
                    .createQuery(
                            "from Hotel h where locate(coalesce(#{hotelSearch.hotel.name},''), h.name) > 0 "
                                    + " and locate(coalesce(#{hotelSearch.hotel.address},''), h.address) > 0 "
                                    + " and (locate(coalesce(#{hotelSearch.location},''),h.city) > 0 "
                                    + " or locate(coalesce(#{hotelSearch.location},''),h.state) > 0 "
                                    + " or locate(coalesce(#{hotelSearch.location},''),h.country) > 0 )")
                    .setMaxResults(criteria.getFetchSize()).setFirstResult(criteria.getFetchOffset()).getResultList();

        } else {
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<Hotel> cquery = builder.createQuery(Hotel.class);
            Root<Hotel> hotel = cquery.from(Hotel.class);
            // QUESTION can like create the pattern for us?
            cquery.select(hotel).where(
                    builder.or(builder.like(builder.lower(hotel.get(Hotel_.name)), criteria.getSearchPattern()),
                            builder.like(builder.lower(hotel.get(Hotel_.city)), criteria.getSearchPattern()),
                            builder.like(builder.lower(hotel.get(Hotel_.zip)), criteria.getSearchPattern()),
                            builder.like(builder.lower(hotel.get(Hotel_.address)), criteria.getSearchPattern())));

            results = entityManager.createQuery(cquery).setMaxResults(criteria.getFetchSize())
                    .setFirstResult(criteria.getFetchOffset()).getResultList();
        }
        nextPageAvailable = results.size() > criteria.getPageSize();
        if (nextPageAvailable) {
            // NOTE create new ArrayList since subList creates unserializable list
            hotels = new ArrayList<Hotel>(results.subList(0, criteria.getPageSize()));
        } else {
            hotels = results;
        }
        log.info(messageBuilder.get().text("Found {0} hotel(s) matching search term [ {1} ] (limit {2})")
                .textParams(hotels.size(), criteria.getQuery(), criteria.getPageSize()).build().getText());
    }

}
