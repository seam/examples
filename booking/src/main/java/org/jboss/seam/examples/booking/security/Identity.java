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

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Named
@SessionScoped
public class Identity implements Serializable {
    private static final long serialVersionUID = 4488275906698680752L;

    private boolean loggedIn;

    @Inject
    private ExternalContext externalContext;

    @Inject
    private Authenticator authenticator;

    @Inject
    private Credentials credentials;

    public String getUsername() {
        return credentials.getUsername();
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void login() {
        if (authenticator.authenticate()) {
            loggedIn = true;
            credentials.setPassword(null);
        }
    }

    public String logout() {
        loggedIn = false;
        HttpSession session = (HttpSession) externalContext.getSession(true);
        session.invalidate();
        return "home?faces-redirect=true";
    }

}
