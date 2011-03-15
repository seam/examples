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
package org.jboss.seam.examples.booking.security;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;

/**
 * This implementation of <strong>Authenticator</strong> cross references the values of the user's credentials against the
 * database.
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Stateless
public class Authenticator {
    @Inject
    private Logger log;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Credentials credentials;

    @Inject
    private Messages messages;

    @Inject
    @Authenticated
    private Event<User> loginEventSrc;

    public boolean authenticate() {
        log.info("Logging in " + credentials.getUsername());
        if ((credentials.getUsername() == null) || (credentials.getPassword() == null)) {
            messages.error(new DefaultBundleKey("identity_loginFailed")).defaults("Invalid username or password");
            return false;
        }

        User user = em.find(User.class, credentials.getUsername());
        if ((user != null) && user.getPassword().equals(credentials.getPassword())) {
            loginEventSrc.fire(user);
            messages.info(new DefaultBundleKey("identity_loggedIn"), user.getName()).defaults("You're signed in as {0}")
                    .params(user.getName());
            return true;
        } else {
            messages.error(new DefaultBundleKey("identity_loginFailed")).defaults("Invalid username or password");
            return false;
        }
    }

}
