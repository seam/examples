package org.jboss.seam.security.examples.simple.ftest;

import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.id;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.ajocado.utils.URLUtils;
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

/**
 * A functional test for a Simple example
 * 
 * @author Martin Gencur
 * @author Jozef Hartinger
 */
@RunWith(Arquillian.class)
public class SimpleTest {
    public static final String ARCHIVE_NAME = "security-simple.war";
    public static final String BUILD_DIRECTORY = "target";
    protected String MAIN_PAGE = "/security-simple/home.jsf";
    protected IdLocator USERNAME_INPUT = id("loginForm:name");
    protected IdLocator PASSWORD_INPUT = id("loginForm:password");
    protected IdLocator LOGIN_BUTTON = id("loginForm:login");
    protected XPathLocator LOGOUT_BUTTON = xp("//input[contains(@id,'logout')]");

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
    public void openStartUrl() {
        selenium.open(URLUtils.buildUrl(contextPath, MAIN_PAGE));
    }

    @Test
    public void testLoginLogout() {
        selenium.type(USERNAME_INPUT, "demo");
        selenium.type(PASSWORD_INPUT, "demo");
        waitForHttp(selenium).click(LOGIN_BUTTON);
        assertTrue("User should be logged in now.", selenium.isTextPresent("Currently logged in as: demo"));
        waitForHttp(selenium).click(LOGOUT_BUTTON);
        assertTrue("User should NOT be logged in.", selenium.isElementPresent(LOGIN_BUTTON));
        assertTrue("User should NOT be logged in.",
                selenium.isTextPresent("Tip: you can login with a username/password of demo/demo."));
    }
}
