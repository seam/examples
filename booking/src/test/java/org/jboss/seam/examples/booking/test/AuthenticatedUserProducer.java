package org.jboss.seam.examples.booking.test;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.model.User;

public class AuthenticatedUserProducer {
    @PersistenceContext
    EntityManager em;

    @Produces
    @Authenticated
    public User getRegisteredUser() {
        return em.find(User.class, "ike");
    }
}
