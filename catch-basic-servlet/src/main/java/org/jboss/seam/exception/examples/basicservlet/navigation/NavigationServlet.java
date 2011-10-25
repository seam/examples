/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.seam.exception.examples.basicservlet.navigation;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.solder.exception.control.ExceptionToCatch;


/**
 * Navigation rules based on Seam Servlet events.
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
@WebServlet(name = "NavigationServlet", urlPatterns = "/Navigation/*")
public class NavigationServlet extends HttpServlet {
    private enum NavigationEnum {
        NULLPOINTEREXCEPTION(new NullPointerException("Null pointer thrown")),
        ASSERTIONERROR(new AssertionError("Assertion Error")),
        IOEXCEPTION(new IOException("IOException")),
        WRAPPEDILLEGALARG(new IllegalStateException("Wrapping IllegalStateException", new IllegalArgumentException("Inner IAE")));

        private final Throwable exception;

        private NavigationEnum(final Throwable e) {
            this.exception = e;
        }

        public Throwable getException() {
            return exception;
        }
    }

    @Inject
    private Event<ExceptionToCatch> catchEvent;

    /**
     * Receives standard HTTP requests from the public <code>service</code> method and dispatches them to the
     * <code>do</code><i>XXX</i> methods defined in this class. This method is an HTTP-specific version of the {@link
     * javax.servlet.Servlet#service} method. There's no need to override this method.
     *
     * @param req  the {@link javax.servlet.http.HttpServletRequest} object that contains the request the client made of
     *             the servlet
     * @param resp the {@link javax.servlet.http.HttpServletResponse} object that contains the response the servlet
     *             returns to the client
     * @throws java.io.IOException            if an input or output error occurs while the servlet is handling the HTTP
     *                                        request
     * @throws javax.servlet.ServletException if the HTTP request cannot be handled
     * @see javax.servlet.Servlet#service
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String uri = req.getRequestURI();

        try {
            final NavigationEnum nav = NavigationEnum.valueOf(uri.substring(uri.lastIndexOf("/") + 1).toUpperCase());

            throw new ServletException(nav.getException()); // wrapping because we don't want to try / catch and we can't add
            // the throws list
        } catch (IllegalArgumentException e) {
            this.catchEvent.fire(new ExceptionToCatch(e)); // If there is no catch integration, this is how to add basic
            // integration yourself
        }
    }
}
