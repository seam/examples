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
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;
import org.jboss.seam.examples.booking.account.Authenticated;
import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.model.SimpleUser;

/**
 * This implementation of a <strong>Authenticator</strong> that uses Seam security.
 * 
 * @author <a href="http://community.jboss.org/people/spinner)">Jose Rodolfo freitas</a>
 */
@Stateless
@Named("bookingAuthenticator")
public class BookingAuthenticator extends BaseAuthenticator implements Authenticator {
	
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

    public void authenticate() {
        log.info("Logging in " + credentials.getUsername());
        if ((credentials.getUsername() == null) || (credentials.getCredential() == null)) {
            messages.error(new DefaultBundleKey("identity_loginFailed")).defaults("Invalid username or password");
            setStatus(AuthenticationStatus.FAILURE);
        }
        User user = em.find(User.class, credentials.getUsername());
        if (user != null && credentials.getCredential() instanceof PasswordCredential){
        	if(user.getPassword().equals(((PasswordCredential) credentials.getCredential()).getValue())) {
	            loginEventSrc.fire(user);
	            messages.info(new DefaultBundleKey("identity_loggedIn"), user.getName()).defaults("You're signed in as {0}")
	                    .params(user.getName());
	            setStatus(AuthenticationStatus.SUCCESS);
	            setUser(new SimpleUser(user.getUsername())); //TODO confirm the need for this set method
	            return;
        	}
        } 
            
        messages.error(new DefaultBundleKey("identity_loginFailed")).defaults("Invalid username or password");
        setStatus(AuthenticationStatus.FAILURE);
        
    }

}
