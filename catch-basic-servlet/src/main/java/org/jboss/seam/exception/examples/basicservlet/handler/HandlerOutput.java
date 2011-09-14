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

/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.seam.exception.examples.basicservlet.handler;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;

/**
 * DRY helper to output the message to the response.
 */
public class HandlerOutput {
    /**
     * Prints the message out to the response
     *
     * @param messages      ResourceBundle to use for messages
     * @param exception     Exception that was caught
     * @param response      response object used to write
     * @param handler       name of handler
     * @param markException method being called from the handler for flow control
     */
    public static void printToResponse(final ResourceBundle messages, final Throwable exception,
                                       final HttpServletResponse response, final String handler,
                                       final String markException) {
        final String output = MessageFormat.format(messages.getString("handler_output"), exception.getClass(),
                handler,
                markException,
                exception.getMessage());
        try {
            response.getWriter().println(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
