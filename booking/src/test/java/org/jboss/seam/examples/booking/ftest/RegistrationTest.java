package org.jboss.seam.examples.booking.ftest;

import static org.jboss.test.selenium.locator.LocatorFactory.jq;
import static org.testng.AssertJUnit.assertTrue;

import java.text.MessageFormat;
import java.util.Date;

import org.jboss.test.selenium.locator.JQueryLocator;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RegistrationTest extends AbstractBookingTest
{
   public static final JQueryLocator LOGIN_REGISTER = jq("[id='login:register']");
   public static final JQueryLocator LOGIN_MESSAGES = jq("[id='login:messages']");
   public static final JQueryLocator REGISTER_USERNAME = jq("[id='username:input']");
   public static final JQueryLocator REGISTER_USERNAME_MESSAGE = jq("[id='username:message1']");
   public static final JQueryLocator REGISTER_NAME = jq("[id='name:input']");
   public static final JQueryLocator REGISTER_EMAIL = jq("[id='email:input']");
   public static final JQueryLocator REGISTER_EMAIL_MESSAGES = jq("[id='email:message1']");
   public static final JQueryLocator REGISTER_PASSWORD = jq("[id='password:input']");
   public static final JQueryLocator REGISTER_PASSWORD_VERIFY = jq("[id='confirmPassword:input']");
   public static final JQueryLocator REGISTER_PASSWORD_VERIFY_MESSAGE = jq("[id='confirmPassword:message1']");
   public static final JQueryLocator REGISTER_SUBMIT = jq("[id='register']");
   public static final JQueryLocator REGISTER_CANCEL = jq("[id='cancel']");
   public static final JQueryLocator REGISTER_MESSAGES = jq("[id='messages']");

   public static final String MESSAGE_SUCCESS = "You have been successfully registered as the user {0}! You may now login.";
   public static final String MESSAGE_INCORRECT_PASSWORD = "Passwords do not match. Please re-type the new password.";
   public static final String MESSAGE_USERNAME_SIZE = "size must be between 3 and 15";
   public static final String MESSAGE_PASSWORD_SIZE = "size must be between 5 and 15";
   public static final String MESSAGE_EMAIL_INCORRECT = "not a well-formed email address";
   public static final String MESSAGE_USERNAME_DUPLICATE = "The username ''{0}'' is already taken. Please choose another username.";
   
   private String usernameSuffix;

   @BeforeClass
   public void generateUsernameSuffix()
   {
      Date date = new Date();
      // suffix is needed to allow tests to be run repeatedly
      usernameSuffix = Long.toString(date.getTime() % 10000000);
   }

   @Override
   @BeforeMethod
   public void setUp()
   {
      selenium.open(contextPath);
      selenium.waitForPageToLoad();
      if (isLoggedIn())
      {
         logout();
      }
      selenium.click(MENU_HOME);
      selenium.waitForPageToLoad();
      selenium.click(LOGIN_REGISTER);
      selenium.waitForPageToLoad();
   }

   @Test
   public void testRegistration()
   {
      String username = "jdoe" + usernameSuffix;
      String password = "password";
      
      populateRegistrationFields(username, "John Doe", "joed@example.com", password, password);
      selenium.click(REGISTER_SUBMIT);
      selenium.waitForPageToLoad();

      String expectedMessage = MessageFormat.format(MESSAGE_SUCCESS, username);
      assertTrue(selenium.getText(LOGIN_MESSAGES).contains(expectedMessage));
      login(username, password);
      assertTrue(isLoggedIn());
      logout();
   }
   
   @Test
   public void testPasswordValidation()
   {
      populateRegistrationFields("tester", "tester", "tester@tester.te", "password", "password1");
      selenium.click(REGISTER_SUBMIT);
      selenium.waitForPageToLoad();
      assertTrue(selenium.getText(REGISTER_MESSAGES).contains(MESSAGE_INCORRECT_PASSWORD));
   }
   
   @Test
   public void testEmailValidation()
   {
       populateRegistrationFields("tester", "tester", "abcdefgh", "password", "password");
       selenium.click(REGISTER_SUBMIT);
       selenium.waitForPageToLoad();
       assertTrue(selenium.getText(REGISTER_EMAIL_MESSAGES).contains(MESSAGE_EMAIL_INCORRECT));
   }
   
   @Test
   public void testUsernameValidation()
   {
      selenium.type(REGISTER_USERNAME, "A");
      selenium.click(REGISTER_SUBMIT);
      selenium.waitForPageToLoad();
      assertTrue(selenium.getText(REGISTER_USERNAME_MESSAGE).contains(MESSAGE_USERNAME_SIZE));
      
      selenium.click(REGISTER_CANCEL);
      selenium.waitForPageToLoad();
      selenium.click(LOGIN_REGISTER);
      selenium.waitForPageToLoad();
      
      selenium.type(REGISTER_USERNAME, "abcdefghijklmnop");
      selenium.click(REGISTER_SUBMIT);
      selenium.waitForPageToLoad();
      assertTrue(selenium.getText(REGISTER_USERNAME_MESSAGE).contains(MESSAGE_USERNAME_SIZE));
      
      // test duplicate username
      populateRegistrationFields("shane", "shane", "shane@example.com", "password", "password");
      selenium.click(REGISTER_SUBMIT);
      selenium.waitForPageToLoad();
      assertTrue(selenium.getText(REGISTER_USERNAME_MESSAGE).contains(MessageFormat.format(MESSAGE_USERNAME_DUPLICATE, "shane")));
   }

   protected void populateRegistrationFields(String username, String name, String email, String password, String verify)
   {
      selenium.type(REGISTER_USERNAME, username);
      selenium.type(REGISTER_NAME, name);
      selenium.type(REGISTER_EMAIL, email);
      selenium.type(REGISTER_PASSWORD, password);
      selenium.type(REGISTER_PASSWORD_VERIFY, verify);
   }
}
