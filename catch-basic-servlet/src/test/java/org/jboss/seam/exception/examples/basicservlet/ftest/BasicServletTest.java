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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;


import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.junit.Assert.assertTrue;

/**
 * A functional test for the Basic Servlet example
 *
 * @author Martin Gencur
 * @author Jozef Hartinger
 */
@RunWith(Arquillian.class)
public class BasicServletTest {
    public static final String ARCHIVE_NAME = "solder-basic-servlet.war";
    public static final String BUILD_DIRECTORY = "target";
    public static final String MAIN_PAGE = "/catch-basic-servlet/index.jsp";
    
    protected XPathLocator NULLPOINTER_LINK = xp("//a[contains(@href,'NullPointerException')]");
    protected XPathLocator ASSERTIONERROR_LINK = xp("//a[contains(@href,'AssertionError')]");
    protected XPathLocator WRAPPEDILLEGALARG_LINK = xp("//a[contains(@href,'WrappedIllegalArg')]");
    protected XPathLocator IOEXCEPTION_LINK = xp("//a[contains(@href,'IOException')]");
    
    @Drone
    AjaxSelenium selenium;

    @ArquillianResource
    URL contextPath;
    
    
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME).importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
                .as(WebArchive.class);
    }

    @Before
    public void openStartUrl() throws MalformedURLException {
        selenium.setSpeed(100);
        selenium.open(new URL(contextPath.toString()));
    }

    @Test
    public void testNullPointerException() {
        waitForHttp(selenium).click(NULLPOINTER_LINK);
        assertTrue("The information about using throwableHandler should appear", selenium.isTextPresent("using handler throwableHandler marking exception with markHandled " +
                "message: Null pointer thrown"));
        assertTrue("The information about using nullPointerHandler should appear", selenium.isTextPresent("using handler nullPointerHandler marking exception with handled " +
                "message: Null pointer thrown"));
    }

    @Test
    public void testAssertionError() {
        waitForHttp(selenium).click(ASSERTIONERROR_LINK);
        assertTrue(selenium.isTextPresent("javax.enterprise.event.ObserverException"));
        assertTrue(selenium.isTextPresent("javax.servlet.ServletException: java.lang.AssertionError: Assertion Error"));
        assertTrue(selenium.isTextPresent("java.lang.AssertionError: Assertion Error"));
    }

    @Test
    public void testIllegalStateException() {
        waitForHttp(selenium).click(WRAPPEDILLEGALARG_LINK);
        assertTrue("The information about using throwableHandler should appear", selenium.isTextPresent("using handler throwableHandler marking exception with markHandled message: " +
                "Inner IAE"));
        assertTrue("The information about using illegalArgumentBreadthFirstHandler should appear", selenium.isTextPresent("using handler illegalArgumentBreadthFirstHandler marking exception with " +
                "dropCause message: Inner IAE"));
        assertTrue("The information about using throwableHandler should appear", selenium.isTextPresent("using handler throwableHandler marking exception with markHandled message: " +
                "Wrapping IllegalStateException"));
        assertTrue("The information about using illegalStateHandler should appear", selenium.isTextPresent("using handler illegalStateHandler marking exception with abort message: " +
                "Wrapping IllegalStateException"));
    }

    @Test
    public void testIOException() {
        waitForHttp(selenium).click(IOEXCEPTION_LINK);
        assertTrue("An exception should have been thrown", selenium.isTextPresent("java.lang.ArithmeticException: Re-thrown"));
    }
}
