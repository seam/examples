package org.jboss.seam.examples.booking.ftest;


import java.io.File;
import java.net.URL;

import org.jboss.arquillian.ajocado.dom.Event;
import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.jboss.arquillian.ajocado.Ajocado.waitForXhr;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.*;
import static org.junit.Assert.assertTrue;

/**
 * Utility methods for the booking example.
 *
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
@RunWith(Arquillian.class)
public abstract class AbstractBookingTest {
    public static final String TITLE = "JBoss Suites: Seam Framework Demo";
    public static final JQueryLocator LOGIN_USERNAME = jq("[id='login:username']");
    public static final JQueryLocator LOGIN_PASSWORD = jq("[id='login:password']");
    public static final JQueryLocator LOGIN_SUBMIT = jq("[id='login:login']");
    public static final JQueryLocator LOGOUT = jq("a:contains('Logout')");
    
    public static final JQueryLocator MENU_FIND = jq("a:contains('Find a Hotel')");
    public static final JQueryLocator MENU_HOME = jq("a:contains('Home')");
    public static final JQueryLocator MENU_ACCOUNT = jq("a:contains('Account')");
    public static final XPathLocator SEARCH_QUERY = xp("//input[contains(@name,'query')]");
    public static final JQueryLocator SEARCH_NO_RESULTS = jq("#noHotelsMsg");
    public static final JQueryLocator SEARCH_PAGE_SIZE = jq("#pageSize");
    public static final XPathLocator SEARCH_RESULT_TABLE_FIRST_ROW_LINK = xp("//a[contains(@name,'hotelSelectionForm:hotels:0:view')]");

    
    public static final String ARCHIVE_NAME = "seam-booking.war";
    public static final String BUILD_DIRECTORY = "target";
    
    private final String DEFAULT_USERNAME = "jose";
    private final String DEFAULT_PASSWORD = "brazil";

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
    public void setUp() {
        selenium.open(contextPath);
        selenium.waitForPageToLoad();
        if (isLoggedIn()) {
            logout();
        }
        login();
        selenium.click(MENU_FIND);
        selenium.waitForPageToLoad();

    }

    public void login() {
        login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public void login(String username, String password) {
        selenium.click(MENU_HOME);
        selenium.waitForPageToLoad();

        selenium.type(LOGIN_USERNAME, username);
        selenium.type(LOGIN_PASSWORD, password);
        selenium.click(LOGIN_SUBMIT);
        selenium.waitForPageToLoad();
       
        assertTrue("Login failed.", isLoggedIn());
    }

    public void logout() {
        if (isLoggedIn()) {
            selenium.click(LOGOUT);
            selenium.waitForPageToLoad();
        }
    }

    public boolean isLoggedIn() {
        return selenium.isElementPresent(LOGOUT);
    }

    public void enterSearchQuery(String query) {
        selenium.type(SEARCH_QUERY, query);
        waitForXhr(selenium).fireEvent(SEARCH_QUERY, Event.KEYUP);
    }
}
