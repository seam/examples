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
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;

/**
 * The view controller for changing the user password
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Stateful
@Model
public class PasswordManager {
    @PersistenceContext
    private EntityManager em;

    @Inject
    private Messages messages;

    @Inject
    @Authenticated
    private User user;

    @NotNull
    @Size(min = 5, max = 15)
    private String confirmPassword;

    private boolean changed;

    public void changePassword() {
        em.merge(user);
        messages.info(new DefaultBundleKey("account_passwordChanged")).defaults("Password successfully updated.");
        changed = true;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setConfirmPassword(final String password) {
        confirmPassword = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

}
