package org.jboss.seam.security.examples.idmconsole.ftest;

import static org.jboss.arquillian.ajocado.Ajocado.elementNotPresent;
import static org.jboss.arquillian.ajocado.Ajocado.waitAjax;
import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.id;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.jboss.arquillian.ajocado.locator.option.OptionLocatorFactory.optionLabel;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.IdLocator;
import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.ajocado.locator.option.OptionLabelLocator;
import org.jboss.arquillian.ajocado.locator.option.OptionLocator;
import org.jboss.arquillian.ajocado.utils.URLUtils;
import org.jboss.arquillian.ajocado.waiting.Wait;
import org.jboss.arquillian.ajocado.waiting.ajax.JavaScriptCondition;
import org.jboss.arquillian.ajocado.waiting.selenium.SeleniumCondition;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A functional test for an IdmConsole example
 * 
 * @author Martin Gencur
 * @author Jozef Hartinger
 */
@RunWith(Arquillian.class)
public class IdmConsoleTest {
    protected String homeUrl = "/security-idmconsole/home.jsf";
    protected IdLocator LOGIN_USERNAME = id("loginForm:name");
    protected IdLocator LOGIN_PASSWORD = id("loginForm:password");
    protected IdLocator LOGIN = id("loginForm:login");
    protected XPathLocator LOGOUT = xp("//a[contains(text(),'Log out')]");
    protected XPathLocator CHANGE_PASSWORD = xp("//a[contains(text(),'Change password')]");

    protected XPathLocator MANAGE_USERS = xp("//a[contains(text(),'Manage users')]");
    protected XPathLocator MANAGE_GROUPS = xp("//a[contains(text(),'Manage groups')]");
    protected XPathLocator MANAGE_ROLES = xp("//a[contains(text(),'Manage role types')]");

    protected XPathLocator CREATE_USER = xp("//a[contains(text(),'Create New User')]");
    protected IdLocator USER_USERNAME = id("user:username");
    protected String userName = "martin";
    protected IdLocator USER_PASSWORD = id("user:password");
    protected IdLocator USER_CONFIRM = id("user:confirm");
    protected IdLocator USER_ENABLED = id("user:enabled");
    protected IdLocator USER_ADD_ROLE = id("user:addRole");
    protected IdLocator USER_SAVE = id("user:save");
    protected IdLocator USER_CANCEL = id("user:cancel");
    protected XPathLocator USER_ADDED = xp("//tbody/tr/td[contains(text(),'" + userName + "')]");

    /* Edit link next to the newly added user */
    protected XPathLocator USER_EDIT = xp("//tbody/tr[4]/td[3]/a[contains(text(),'Edit')]");
    /* Delete link next to the newly added user */
    protected XPathLocator USER_DELETE = xp("//tbody/tr[4]/td[3]/a[contains(text(),'Delete')]");
    protected IdLocator ROLE_TYPE = id("role:roleType");
    protected IdLocator ROLE_GROUP = id("role:roleGroup");
    protected OptionLocator<OptionLabelLocator> ADMIN_ROLE = optionLabel("admin");
    protected OptionLocator<OptionLabelLocator> HEAD_GROUP = optionLabel("Head Office");
    protected IdLocator ROLE_ADD = id("role:add");

    protected XPathLocator CREATE_GROUP = xp("//a[contains(text(),'Create new group')]");
    protected IdLocator GROUP_NAME = id("group:groupname");
    protected IdLocator GROUP_SAVE = id("group:save");
    protected String groupName = "PittsburghPenguins";
    protected XPathLocator GROUP_ADDED = xp("//tbody/tr/td[contains(text(),'" + groupName + "')]");
    /* Delete link next to the newly added group */
    protected XPathLocator GROUP_DELETE = xp("//tbody/tr[2]/td[2]/a[contains(text(),'Delete')]");

    protected XPathLocator CREATE_ROLE = xp("//a[contains(text(),'Create new role')]");
    protected IdLocator ROLE_NAME = id("role:roleType");
    protected IdLocator ROLE_SAVE = id("role:add");
    protected String roleName = "accountant";
    protected XPathLocator ROLE_ADDED = xp("//tbody/tr/td[contains(text(),'" + roleName + "')]");
    /* Delete link next to the newly added role */
    protected XPathLocator ROLE_DELETE = xp("//tbody/tr[3]/td[2]/a[contains(text(),'Delete')]");

    protected IdLocator OLD_PASSWORD = id("changepassword:oldPassword");
    protected IdLocator NEW_PASSWORD = id("changepassword:newPassword");
    protected IdLocator CONFIRM_PASSWORD = id("changepassword:confirmPassword");
    protected IdLocator PASSWORD_SAVE = id("changepassword:save");

    protected String defaultUser = "demo";
    protected String defaultPassword = "demo";
    protected String newPassword = "newpassword";

    public static final String ARCHIVE_NAME = "security-idmconsole.war";
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
    public void setup() {
        selenium.open(URLUtils.buildUrl(contextPath, homeUrl));
        login();
    }

    @After
    public void tearDown() {
        logout();
    }

    @Test
    public void test() {
        // test creating group
        deleteGroup(); // in case the group already exists
        waitForHttp(selenium).click(MANAGE_GROUPS);
        waitForHttp(selenium).click(CREATE_GROUP);
        selenium.type(GROUP_NAME, groupName);
        waitForHttp(selenium).click(GROUP_SAVE);
        assertTrue("Group should be added now", selenium.isElementPresent(GROUP_ADDED));

        // test deleting group
        deleteGroup();
        assertFalse("Group should be removed now", selenium.isElementPresent(GROUP_ADDED));

        // test creating user
        deleteUser(); // in case the user already exists
        waitForHttp(selenium).click(MANAGE_USERS);
        waitForHttp(selenium).click(CREATE_USER);
        selenium.type(USER_USERNAME, userName);
        selenium.type(USER_PASSWORD, "mypassword");
        selenium.type(USER_CONFIRM, "mypassword");
        selenium.check(USER_ENABLED);
        waitForHttp(selenium).click(USER_SAVE);
        assertTrue("User should be added now", selenium.isElementPresent(USER_ADDED));

        // test adding new role
        waitForHttp(selenium).click(MANAGE_USERS);
        waitForHttp(selenium).click(USER_EDIT);
        waitForHttp(selenium).click(USER_ADD_ROLE);
        selenium.select(ROLE_TYPE, ADMIN_ROLE);
        selenium.select(ROLE_GROUP, HEAD_GROUP);
        waitForHttp(selenium).click(ROLE_ADD);
        assertTrue("'admin' role should be added now", selenium.isTextPresent("admin in group Head Office"));

        // test deleting user
        deleteUser();
        assertFalse("User should be removed now", selenium.isElementPresent(USER_EDIT));
    }

    private void deleteGroup() {
        waitForHttp(selenium).click(MANAGE_GROUPS);
        if (selenium.isElementPresent(GROUP_DELETE)) {
            selenium.chooseOkOnNextConfirmation();
            selenium.click(GROUP_DELETE);
            waitForConfirmation(elementNotPresent.locator(GROUP_DELETE));
        }
    }

    private void deleteUser() {
        waitForHttp(selenium).click(MANAGE_USERS);
        if (selenium.isElementPresent(USER_DELETE)) {
            selenium.chooseOkOnNextConfirmation();
            selenium.click(USER_DELETE);
            waitForConfirmation(elementNotPresent.locator(USER_DELETE));
        }
    }

    @Test
    public void testRoles() {
        // test creating role
        deleteRole();
        waitForHttp(selenium).click(MANAGE_ROLES);
        waitForHttp(selenium).click(CREATE_ROLE);
        selenium.type(ROLE_NAME, roleName);
        waitForHttp(selenium).click(ROLE_SAVE);
        assertTrue("Role should be added now", selenium.isElementPresent(ROLE_ADDED));

        // test deleting role
        deleteRole(); // in case the role already exists
        assertFalse("Role should be removed now", selenium.isElementPresent(ROLE_ADDED));
    }

    private void deleteRole() {
        waitForHttp(selenium).click(MANAGE_ROLES);
        if (selenium.isElementPresent(ROLE_DELETE)) {
            selenium.chooseOkOnNextConfirmation();
            selenium.click(ROLE_DELETE);
            waitForConfirmation(elementNotPresent.locator(ROLE_DELETE));
        }
    }

    /**
     * The method waits for confirmation to appear, consumes the confirmation and then waits until the condition passed as a
     * method parameter to become satisfied.
     */
    private void waitForConfirmation(JavaScriptCondition condition) {
        Wait.waitSelenium.timeout(10000).interval(50).until(new SeleniumCondition() {
            @Override
            public boolean isTrue() {
                return selenium.isConfirmationPresent();
            }
        });
        selenium.getConfirmation();
        waitAjax.until(condition);
    }

    @Test
    public void testChangePassword() {
        changePassword(defaultPassword, newPassword);
        logout();
        login(defaultUser, newPassword);
        changePassword(newPassword, defaultPassword); // change the password back to original
    }

    private void changePassword(String defaultPassword, String newPassword) {
        waitForHttp(selenium).click(CHANGE_PASSWORD);
        selenium.type(OLD_PASSWORD, defaultPassword);
        selenium.type(NEW_PASSWORD, newPassword);
        selenium.type(CONFIRM_PASSWORD, newPassword);
        waitForHttp(selenium).click(PASSWORD_SAVE);
    }

    private void logout() {
        waitForHttp(selenium).click(LOGOUT);
    }

    private void login() {
        login(defaultUser, defaultPassword);
    }

    private void login(String userName, String password) {
        selenium.type(LOGIN_USERNAME, userName);
        selenium.type(LOGIN_PASSWORD, password);
        waitForHttp(selenium).click(LOGIN);
        assertTrue("Login was not successful", selenium.isElementPresent(LOGOUT));
    }
}
