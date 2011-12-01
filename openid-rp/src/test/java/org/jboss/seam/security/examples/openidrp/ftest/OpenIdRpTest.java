package org.jboss.seam.security.examples.openidrp.ftest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;


import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
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

import static org.jboss.arquillian.ajocado.Ajocado.elementPresent;
import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.Ajocado.waitModel;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.id;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * A functional test for the openid-rp example
 *
 * @author Martin Gencur
 * @author Jozef Hartinger
 */
@RunWith(Arquillian.class)
public class OpenIdRpTest {
    private static Properties properties = new Properties();
    private static boolean propertiesLoaded = false;
    private static String PROPERTY_FILE = "ftest.properties";
    private static final int checkInterval = 1000;
    private static final int modelTimeout = 30000;
    protected XPathLocator LOGIN_LINK = xp("//a[contains(text(),'Login')]");
    protected XPathLocator LOGOUT_LINK = xp("//a[contains(text(),'Logout')]");
    protected XPathLocator LAST_CELL = xp("//div[@class='content']/table/tbody/tr[3]/td[2]");
    protected XPathLocator MYOPENID_RADIO = xp("//input[@type='radio'][@value='myopenid']");
    protected XPathLocator GOOGLE_RADIO = xp("//input[@type='radio'][@value='google']");
    protected XPathLocator YAHOO_RADIO = xp("//input[@type='radio'][@value='yahoo']");
    protected XPathLocator CUSTOM_RADIO = xp("//input[@type='radio'][@value='custom']");
    protected XPathLocator ADDRESS_FIELD = xp("//input[@type='text']");
    protected XPathLocator SUBMIT_BUTTON = xp("//input[@value='login']");
    protected IdLocator MYOPENID_USERNAME = id("identity");
    protected IdLocator MYOPENID_PASSWORD = id("password");
    protected IdLocator MYOPENID_SIGN_IN = id("signin_button");
    protected IdLocator GOOGLE_USERNAME = id("Email");
    protected IdLocator GOOGLE_PASSWORD = id("Passwd");
    protected IdLocator GOOGLE_SIGN_IN = id("signIn");
    protected IdLocator GOOGLE_APPROVAL = id("approve_button");
    protected IdLocator YAHOO_USERNAME = id("username");
    protected IdLocator YAHOO_PASSWORD = id("passwd");
    protected IdLocator YAHOO_SIGN_IN = id(".save");
    protected IdLocator YAHOO_APPROVAL = id("agree");
    public static final String ARCHIVE_NAME = "security-openid-rp.war";
    public static final String BUILD_DIRECTORY = "target";
    
    @Drone
    private AjaxSelenium selenium;

    @ArquillianResource
    private URL contextPath;

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
//    @Test(dependsOnMethods = {"testGoogle"}) not sure why this had a dependency on another one
    public void testMyOpenID() {
        waitForHttp(selenium).click(LOGIN_LINK);
        selenium.check(MYOPENID_RADIO);
        selenium.click(SUBMIT_BUTTON);
        waitModel.interval(checkInterval).timeout(modelTimeout).until(elementPresent.locator(MYOPENID_USERNAME));
        selenium.type(MYOPENID_USERNAME, getProperty("myopenid.username"));
        selenium.type(MYOPENID_PASSWORD, getProperty("myopenid.password"));
        selenium.click(MYOPENID_SIGN_IN);
        checkMyOpenIdSignedIn();
    }

    @Test
    public void testGoogle() {
        waitForHttp(selenium).click(LOGIN_LINK);
        selenium.check(GOOGLE_RADIO);
        selenium.click(SUBMIT_BUTTON);
        waitModel.interval(checkInterval).timeout(modelTimeout).until(elementPresent.locator(GOOGLE_USERNAME));
        selenium.type(GOOGLE_USERNAME, getProperty("google.username"));
        selenium.type(GOOGLE_PASSWORD, getProperty("google.password"));
        waitForHttp(selenium).click(GOOGLE_SIGN_IN);
        if (selenium.isElementPresent(GOOGLE_APPROVAL)) {
            waitForHttp(selenium).click(GOOGLE_APPROVAL);
        }
        waitModel.interval(checkInterval).timeout(modelTimeout / 2).until(elementPresent.locator(LAST_CELL));
        assertTrue("User should be verified now!", selenium.isTextPresent("Verified User Identifier"));
        assertTrue("OpendID Provider info should be displayed", selenium.isTextPresent("OpenID Provider"));
        assertTrue("OpendID Provider name should be displayed", selenium.isTextPresent("https://www.google.com/accounts/"));
        waitForHttp(selenium).click(LOGOUT_LINK);
        assertFalse("User should be logged out now", selenium.isTextPresent("https://www.google.com/accounts/"));
    }

    @Test
    public void testYahoo() {
        waitForHttp(selenium).click(LOGIN_LINK);
        selenium.check(YAHOO_RADIO);
        selenium.click(SUBMIT_BUTTON);
        waitModel.interval(checkInterval).timeout(modelTimeout).until(elementPresent.locator(YAHOO_USERNAME));
        selenium.type(YAHOO_USERNAME, getProperty("yahoo.username"));
        selenium.type(YAHOO_PASSWORD, getProperty("yahoo.password"));
        waitForHttp(selenium).click(YAHOO_SIGN_IN);
        if (selenium.isElementPresent(YAHOO_APPROVAL)) {
            waitForHttp(selenium).click(YAHOO_APPROVAL);
        }
        waitModel.interval(checkInterval).timeout(modelTimeout / 2).until(elementPresent.locator(LAST_CELL));
        assertTrue("User should be verified now!", selenium.isTextPresent("Verified User Identifier"));
        assertTrue("OpendID Provider info should be displayed", selenium.isTextPresent("OpenID Provider"));
        assertTrue("OpendID Provider name should be displayed", selenium.isTextPresent("https://open.login.yahooapis.com/openid/op/auth"));
        waitForHttp(selenium).click(LOGOUT_LINK);
        assertFalse("User should be logged out now", selenium.isTextPresent("https://open.login.yahooapis.com/openid/op/auth"));
    }

    @Test
//    @Test(dependsOnMethods = {"testMyOpenID"}) not sure why this had a dependency on another one
    public void testCustom() {
        waitForHttp(selenium).click(LOGIN_LINK);
        selenium.check(CUSTOM_RADIO);
        selenium.type(ADDRESS_FIELD, "https://www.myopenid.com/");
        selenium.click(SUBMIT_BUTTON);
        checkMyOpenIdSignedIn();
    }

    public void checkMyOpenIdSignedIn() {
        waitModel.interval(checkInterval).timeout(modelTimeout / 2).until(new SeleniumCondition() {
            @Override
            public boolean isTrue() {
                return selenium.isTextPresent("Email") && selenium.isElementPresent(LAST_CELL);
            }
        });
        assertTrue("User should be verified now!", selenium.isTextPresent("Verified User Identifier"));
        assertTrue("The user identifier should be displayed", selenium.isTextPresent("https://" + getProperty("myopenid.username") + "/"));
        assertTrue("OpendID Provider info should be displayed", selenium.isTextPresent("OpenID Provider"));
        assertTrue("OpendID Provider name should be displayed", selenium.isTextPresent("https://www.myopenid.com/server"));
        waitForHttp(selenium).click(LOGOUT_LINK);
        assertFalse("User should be logged out now", selenium.isTextPresent("https://www.myopenid.com/server"));
    }

    public String getProperty(String key) {
        if (!propertiesLoaded) {
            try {
                properties.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE));
                propertiesLoaded = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties.getProperty(key, "Property not found: " + key);
    }
}
