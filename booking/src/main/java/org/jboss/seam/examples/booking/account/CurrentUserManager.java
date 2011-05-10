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
package org.jboss.seam.examples.booking.account;

import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.examples.booking.model.User;

/**
 * Exposes the currently logged in user
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Stateful
@SessionScoped
public class CurrentUserManager {
    private User currentUser;

    @Produces
    @Authenticated
    @Named("currentUser")
    public User getCurrentAccount() {
        return currentUser;
    }

    // Injecting HttpServletRequest instead of HttpSession as the latter conflicts with a Weld bean on GlassFish 3.0.1
    public void onLogin(@Observes @Authenticated User user, HttpServletRequest request) {
        currentUser = user;
        // reward authenticated users with a longer session
        // default is kept short to prevent search engines from driving up # of sessions
        request.getSession().setMaxInactiveInterval(3600);
    }
}
