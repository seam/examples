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
package org.jboss.seam.exception.examples.basicservlet.ftest;

import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.test.selenium.AbstractTestCase;
import org.jboss.test.selenium.locator.XpathLocator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.jboss.test.selenium.guard.request.RequestTypeGuardFactory.waitHttp;
import static org.jboss.test.selenium.locator.LocatorFactory.xp;
import static org.testng.Assert.assertTrue;

/**
 * A functional test for the Basic Servlet example
 *
 * @author Martin Gencur
 */
public class BasicServletTest extends AbstractTestCase {
    protected XpathLocator NULLPOINTER_LINK = xp("//a[contains(@href,'NullPointerException')]");
    protected XpathLocator ASSERTIONERROR_LINK = xp("//a[contains(@href,'AssertionError')]");
    protected XpathLocator WRAPPEDILLEGALARG_LINK = xp("//a[contains(@href,'WrappedIllegalArg')]");
    protected XpathLocator IOEXCEPTION_LINK = xp("//a[contains(@href,'IOException')]");

    @BeforeMethod
    public void openStartUrl() throws MalformedURLException {
        selenium.setSpeed(100);
        selenium.open(new URL(contextPath.toString()));
    }

    @Test
    public void testNullPointerException() {
        waitHttp(selenium).click(NULLPOINTER_LINK);
        assertTrue(selenium.isTextPresent("using handler throwableHandler marking exception with markHandled " +
                "message: Null pointer thrown"),
                "The information about using throwableHandler should appear");
        assertTrue(selenium.isTextPresent("using handler nullPointerHandler marking exception with handled " +
                "message: Null pointer thrown"),
                "The information about using nullPointerHandler should appear");
    }

    @Test
    public void testAssertionError() {
        waitHttp(selenium).click(ASSERTIONERROR_LINK);
        assertTrue(selenium.isTextPresent("javax.enterprise.event.ObserverException"));
        assertTrue(selenium.isTextPresent("javax.servlet.ServletException: java.lang.AssertionError: Assertion Error"));
        assertTrue(selenium.isTextPresent("java.lang.AssertionError: Assertion Error"));
    }

    @Test
    public void testIllegalStateException() {
        waitHttp(selenium).click(WRAPPEDILLEGALARG_LINK);
        assertTrue(selenium.isTextPresent("using handler throwableHandler marking exception with markHandled message: " +
                "Inner IAE"), "The information about using throwableHandler should appear");
        assertTrue(selenium.isTextPresent("using handler illegalArgumentBreadthFirstHandler marking exception with " +
                "dropCause message: Inner IAE"),
                "The information about using illegalArgumentBreadthFirstHandler should appear");
        assertTrue(selenium.isTextPresent("using handler throwableHandler marking exception with markHandled message: " +
                "Wrapping IllegalStateException"),
                "The information about using throwableHandler should appear");
        assertTrue(selenium.isTextPresent("using handler illegalStateHandler marking exception with abort message: " +
                "Wrapping IllegalStateException"),
                "The information about using illegalStateHandler should appear");
    }

    @Test
    public void testIOException() {
        waitHttp(selenium).click(IOEXCEPTION_LINK);
        assertTrue(selenium.isTextPresent("java.lang.ArithmeticException: Re-thrown"), "An exception should have been thrown");
    }
}
