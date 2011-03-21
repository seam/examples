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
package org.jboss.seam.examples.booking.support;

import org.jboss.logging.Logger;

/**
 * Provide a completely empty logger implementation for Unit Tests. This entire class is a no-op; it does <i>nothing</i>.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com>Lincoln Baxter, III</a>
 * 
 */
public class NoOpLogger extends Logger {

    protected NoOpLogger(String name) {
        super(name);
    }

    @Override
    public boolean isEnabled(Level level) {
        return false;
    }

    @Override
    protected void doLog(Level level, String loggerClassName, Object message, Object[] parameters, Throwable thrown) {
    }

    @Override
    protected void doLogf(Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
    }

}
