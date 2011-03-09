/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.examples.booking.ftest;

import org.jboss.test.selenium.locator.JQueryLocator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.jboss.test.selenium.locator.LocatorFactory.jq;
import static org.testng.AssertJUnit.*;

/**
 * This class tests change password funcionality.
 * 
 * @author jbalunas
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ChangePasswordTest extends AbstractBookingTest
{

   public static final JQueryLocator BUTTON_CHANGE_PASSWORD = jq("[id='changePassword']");
   public static final JQueryLocator PASSWORD_CURRENT = jq("[id='current:input']");
   public static final JQueryLocator PASSWORD_NEW = jq("[id='new:input']");
   public static final JQueryLocator PASSWORD_NEW_VERIFY = jq("[id='confirm:input']");
   public static final JQueryLocator PASSWORD_SUBMIT = jq("[id='change']");
   public static final JQueryLocator PASSWORD_CANCEL = jq("[id='cancel']");
   public static final JQueryLocator MESSAGES = jq("[id='messages']");
   public static final JQueryLocator MESSAGES_NEW_PASSWORD = jq("[id='new:message1']");
   public static final JQueryLocator MESSAGES_VERIFY = jq("[id='confirm:message1']");
   
   public static final String MESSAGE_SUCCESS = "Password successfully updated.";
   public static final String MESSAGE_NO_MATCH = "Passwords do not match. Please re-type the new password.";
   public static final String MESSAGE_SIZE = "size must be between 5 and 15";
   
   private final static String LONG_TEXT = "testertestertest";
   private final static String SHORT_TEXT = "tt";
   private final String DEFAULT_USERNAME = "lincoln";
   private final String DEFAULT_PASSWORD = "charlotte";

   
   
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
   }

   /**
    * Verifies that changing password works well. If clean-up part of this
    * method fails it may affect other methods.
    */
   @Test
   public void testPasswordChange()
   {
      String username = "shane";
      String password = "brisbane";
      String newPassword = "password";
      
      login(username, password);
      changePassword(password, newPassword, newPassword);
      
      assertTrue("Password change not confirmed: " + selenium.getText(MESSAGES), selenium.getText(MESSAGES).contains(MESSAGE_SUCCESS));
      
      logout();
      login(username, newPassword);
      // change the password back so that the test has no side-effects
      changePassword(newPassword, password, password);
      
      assertTrue(selenium.getText(MESSAGES).contains(MESSAGE_SUCCESS));
   }
   
   @Test
   public void testCancelButton()
   {
      String username = "jose";
      String password = "brazil";
      
      login(username, password);
      selenium.click(MENU_ACCOUNT);
      selenium.waitForPageToLoad();
      selenium.click(BUTTON_CHANGE_PASSWORD);
      selenium.waitForPageToLoad();
      fillPasswordForm(password, "password", "password");
      selenium.click(PASSWORD_CANCEL);
      selenium.waitForPageToLoad();
      
      logout();
      login(username, password);
   }
   
   @Test
   public void testNonTrivialScenario()
   {
      String username = "dan";
      String password = "laurel";
      String newPassword = "password";
      
      login(username, password);
      // start with an invalid combination
      changePassword(password, "foobar", "barfoo");
      assertTrue("Password verification failed", selenium.getText(MESSAGES).contains(MESSAGE_NO_MATCH));
      // invalid combination once again
      changePassword(password, "barfoo", "foobar");
      assertTrue("Password verification failed", selenium.getText(MESSAGES).contains(MESSAGE_NO_MATCH));
      // this should pass
      changePassword(password, newPassword, newPassword);
      assertTrue("Password change not confirmed: " + selenium.getText(MESSAGES), selenium.getText(MESSAGES).contains(MESSAGE_SUCCESS));
      // set the password back - no side-effects
      changePassword(newPassword, password, password);
   }

   @Test
   public void testDifferentPasswords()
   {
      login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
      changePassword(DEFAULT_USERNAME, "foobar", "barfoo");
      assertTrue("Password verification failed", selenium.getText(MESSAGES).contains(MESSAGE_NO_MATCH));
      logout();
   }

   @Test
   public void testEmptyPasswords()
   {
      login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
      changePassword(DEFAULT_PASSWORD, "", "");
      assertTrue("Password validation failed", selenium.getText(MESSAGES_NEW_PASSWORD).contains(MESSAGE_SIZE));
      assertTrue("Password validation failed", selenium.getText(MESSAGES_VERIFY).contains(MESSAGE_SIZE));
      logout();
   }

   @Test
   public void testLongPassword()
   {
      login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
      changePassword(DEFAULT_PASSWORD, LONG_TEXT, LONG_TEXT);
      assertTrue("Password validation failed", selenium.getText(MESSAGES_NEW_PASSWORD).contains(MESSAGE_SIZE));
      assertTrue("Password validation failed", selenium.getText(MESSAGES_VERIFY).contains(MESSAGE_SIZE));
      logout();
   }

   @Test
   public void testShortPassword()
   {
      login(DEFAULT_USERNAME, DEFAULT_PASSWORD);
      changePassword(DEFAULT_PASSWORD, SHORT_TEXT, SHORT_TEXT);
      assertTrue("Password validation failed", selenium.getText(MESSAGES_NEW_PASSWORD).contains(MESSAGE_SIZE));
      assertTrue("Password validation failed", selenium.getText(MESSAGES_VERIFY).contains(MESSAGE_SIZE));
      logout();
   }

   public void changePassword(String currentPassword, String newPassword, String newPasswordVerify)
   {
      selenium.click(MENU_ACCOUNT);
      selenium.waitForPageToLoad();
      selenium.click(BUTTON_CHANGE_PASSWORD);
      selenium.waitForPageToLoad();
      fillPasswordForm(currentPassword, newPassword, newPasswordVerify);
      selenium.click(PASSWORD_SUBMIT);
      selenium.waitForPageToLoad();
   }
   
   public void fillPasswordForm(String currentPassword, String newPassword, String newPasswordVerify)
   {
      selenium.type(PASSWORD_CURRENT, currentPassword);
      selenium.type(PASSWORD_NEW, newPassword);
      selenium.type(PASSWORD_NEW_VERIFY, newPasswordVerify);
   }
}
