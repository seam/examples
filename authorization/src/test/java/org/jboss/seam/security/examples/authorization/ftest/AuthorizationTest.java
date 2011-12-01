package org.jboss.seam.security.examples.authorization.ftest;

import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.id;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
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
 * A functional test for the Authorization example
 * 
 * @author Martin Gencur
 * @author Marek Schmidt
 * 
 */
@RunWith(Arquillian.class)
public class AuthorizationTest
{
    public static final String ARCHIVE_NAME = "security-authorization.war";
    public static final String BUILD_DIRECTORY = "target";
    public static final String HOME_PAGE = "/security-authorization/home.jsf";

    protected IdLocator USERNAME_INPUT = id("loginForm:name");
    protected IdLocator LOGIN = id("loginForm:login");
    protected XPathLocator LOGOUT = xp("//a[contains(text(),'Log out')]");
    protected XPathLocator DO_SOMETHING_RESTRICTED = xp("//input[contains(@value,'doSomethingRestricted')]");
    protected XPathLocator DO_FOO_ABC = xp("//input[contains(@value,'doFooAbc')]");
    protected XPathLocator DO_FOO_DEF = xp("//input[contains(@value,'doFooDef')]");
    protected XPathLocator DO_LOGGED_IN = xp("//input[contains(@value,'doLoggedIn')]");
    protected XPathLocator DO_USER_ACTION = xp("//input[contains(@value,'doUserAction')]");
    protected XPathLocator DO_DEMO_USER_RULE_ACTION = xp("//input[contains(@value,'doDemoUserRuleAction')]");
    protected XPathLocator DO_USER_GROUP_RULE_ACTION = xp("//input[contains(@value,'doInUserGroupRuleAction')]");
    private final String WARNING = "You do not have the necessary permissions to perform that operation";
    private final String WARNING_INFO = "You do not have the necessary permissions to perform that operation";
    private final String GENERAL_USER = "martin";
    private final String ADMIN_USER = "demo";
    private final String MEMBER_USER = "user";

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
    public void openStartUrl() throws MalformedURLException
    {
        selenium.open(URLUtils.buildUrl(contextPath, HOME_PAGE));
    }

    @Test
    public void testSomethingRestricted()
    {
        //not logged in
        waitForHttp(selenium).click(DO_SOMETHING_RESTRICTED);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        //general user
        login(GENERAL_USER);
        waitForHttp(selenium).click(DO_SOMETHING_RESTRICTED);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //member user
        login(MEMBER_USER);
        waitForHttp(selenium).click(DO_SOMETHING_RESTRICTED);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //admin user
        login(ADMIN_USER);
        waitForHttp(selenium).click(DO_SOMETHING_RESTRICTED);
        assertTrue("doSomethingRestricted method should be invoked", selenium.isTextPresent("doSomethingRestricted() invoked"));
        waitForHttp(selenium).click(LOGOUT);
    }
    
    @Test
    public void testFooAbc()
    {
        waitForHttp(selenium).click(DO_FOO_ABC);
        assertTrue("doFooAbc method should be invoked", selenium.isTextPresent("doFooAbc() invoked"));
    }
    
    @Test
    public void testFooDef()
    {
        //not logged in
        waitForHttp(selenium).click(DO_FOO_DEF);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        //general user
        login(GENERAL_USER);
        waitForHttp(selenium).click(DO_FOO_DEF);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //member user
        login(MEMBER_USER);
        waitForHttp(selenium).click(DO_FOO_DEF);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //admin user
        login(ADMIN_USER);
        waitForHttp(selenium).click(DO_FOO_DEF);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
    }
    
    @Test
    public void testLoggedIn()
    {
        //not logged in
        waitForHttp(selenium).click(DO_LOGGED_IN);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        //general user
        login(GENERAL_USER);
        waitForHttp(selenium).click(DO_LOGGED_IN);
        assertTrue("doLoggedIn method should be invoked", selenium.isTextPresent("doLoggedIn() invoked"));
        waitForHttp(selenium).click(LOGOUT);
        //member user
        login(MEMBER_USER);
        waitForHttp(selenium).click(DO_LOGGED_IN);
        assertTrue("doLoggedIn method should be invoked", selenium.isTextPresent("doLoggedIn() invoked"));
        waitForHttp(selenium).click(LOGOUT);
        //admin user
        login(ADMIN_USER);
        waitForHttp(selenium).click(DO_LOGGED_IN);
        assertTrue("doLoggedIn method should be invoked", selenium.isTextPresent("doLoggedIn() invoked"));
        waitForHttp(selenium).click(LOGOUT);
    }
    
    @Test
    public void testUserAction()
    {
        //not logged in
        waitForHttp(selenium).click(DO_USER_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        //general user
        login(GENERAL_USER);
        waitForHttp(selenium).click(DO_USER_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //member user
        login(MEMBER_USER);
        waitForHttp(selenium).click(DO_USER_ACTION);
        assertTrue("doUserAction method should be invoked", selenium.isTextPresent("doUserAction() invoked"));
        waitForHttp(selenium).click(LOGOUT);
        //admin user
        login(ADMIN_USER);
        waitForHttp(selenium).click(DO_USER_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
    }
    
    @Test
    public void testDemoUserRuleAction()
    {
        // not logged in
        waitForHttp(selenium).click(DO_DEMO_USER_RULE_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        //general user
        login(GENERAL_USER);
        waitForHttp(selenium).click(DO_DEMO_USER_RULE_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //member user
        login(MEMBER_USER);
        waitForHttp(selenium).click(DO_DEMO_USER_RULE_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //admin user
        login(ADMIN_USER);
        waitForHttp(selenium).click(DO_DEMO_USER_RULE_ACTION);
        assertTrue("doUserAction method should be invoked", selenium.isTextPresent("doDemoUserRuleAction() invoked"));
        waitForHttp(selenium).click(LOGOUT);
    }
    
    @Test
    public void testUserGroupRuleAction()
    {
        // not logged in
        waitForHttp(selenium).click(DO_USER_GROUP_RULE_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        //general user
        login(GENERAL_USER);
        waitForHttp(selenium).click(DO_USER_GROUP_RULE_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
        //member user
        login(MEMBER_USER);
        waitForHttp(selenium).click(DO_USER_GROUP_RULE_ACTION);
        assertTrue("doUserAction method should be invoked", selenium.isTextPresent("doInUserGroupRuleAction() invoked "));
        waitForHttp(selenium).click(LOGOUT);
        //admin user
        login(ADMIN_USER);
        waitForHttp(selenium).click(DO_USER_GROUP_RULE_ACTION);
        assertTrue(WARNING_INFO, selenium.isTextPresent(WARNING));
        waitForHttp(selenium).click(LOGOUT);
    }
    
    private void login(String name)
    {
        selenium.type(USERNAME_INPUT, name);
        waitForHttp(selenium).click(LOGIN);
    }
}
