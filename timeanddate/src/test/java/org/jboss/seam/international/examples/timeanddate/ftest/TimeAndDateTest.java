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
package org.jboss.seam.international.examples.timeanddate.ftest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.jboss.arquillian.ajocado.locator.option.OptionLocatorFactory.optionLabel;
import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.Ajocado.waitForXhr;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

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



/**
 * A functional test for a TimeAndDate example
 *
 * @author <a href="http://community.jboss.org/people/mgencur">Martin Gencur</a>
 */
@RunWith(Arquillian.class)
public class TimeAndDateTest{
    protected XPathLocator WORLDSCLOCK_LINK = xp("//a[contains(text(),'worldsclock')]");
    protected XPathLocator HOME_LINK = xp("//a[contains(text(),'home')]");
    protected XPathLocator AFRICA_LINK = xp("//a[contains(text(),'Africa')]");
    protected XPathLocator AMERICA_LINK = xp("//a[contains(text(),'America')]");
    protected XPathLocator ASIA_LINK = xp("//a[contains(text(),'Asia')]");
    protected XPathLocator ATLANTIC_LINK = xp("//a[contains(text(),'Atlantic')]");
    protected XPathLocator AUSTRALIA_LINK = xp("//a[contains(text(),'Australia')]");
    protected XPathLocator EUROPE_LINK = xp("//a[contains(text(),'Europe')]");
    protected XPathLocator PACIFIC_LINK = xp("//a[contains(text(),'Pacific')]");
    protected XPathLocator DATETIME_INFO = xp("//table/tbody/tr[1]/td[2]");
    protected XPathLocator TIMEZONE_INFO = xp("/html/body/div[3]/div/form/span");
    protected XPathLocator LOCALE_SELECT = xp("//select");
    
    protected String EN_LOCALE_LABEL_EN = "English";
    protected String EN_LOCALE_LABEL_PT_BR = "Portuguese (Brazil)";
    
    protected String PT_BR_LOCALE_LABEL_EN = "inglês";
    protected String PT_BR_LOCALE_LABEL_PT_BR = "português (Brasil)";

    private static final String FIRST_ENTRY_ERR = "First entry in the table not found";
    private static final String LAST_ENTRY_ERR = "Last entry in the table not found";
    private static final String STANDARD_DATE_TIME_FORMAT = "HH:mm:ss MM/dd/yyyy";
    private static final SimpleDateFormat formatter = new SimpleDateFormat(STANDARD_DATE_TIME_FORMAT);
    
    private static final String EN_INFO = "This example application demonstrates several features of the Seam International module.";
    private static final String PT_BR_INFO = "Essa é uma aplicação exemplo que mostra algumas funções do seam-international";
    
    private static final String LOCALE_CHANGE_PT_BR = "Você mudou a sua localidade para português (Brasil).";
    private static final String LOCALE_CHANGE_EN = "You have changed your Locale to English.";
    
    public static final String ARCHIVE_NAME = "international-timeanddate.war";
    public static final String BUILD_DIRECTORY = "target";
    
    
    @ArquillianResource
    URL contextPath;
    
    @Drone
    AjaxSelenium selenium;
    
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(ZipImporter.class, ARCHIVE_NAME).importFrom(new File(BUILD_DIRECTORY + '/' + ARCHIVE_NAME))
                .as(WebArchive.class);
    }
    
    @Before
    public void openStartUrl() throws MalformedURLException {
        selenium.open(new URL(contextPath.toString()));
    }

    @Test
    public void testHomeLink() {
        waitForHttp(selenium).click(HOME_LINK);
        assertTrue("The page should contain a description of the application", selenium.isTextPresent(EN_INFO));
    }

    @Test
    public void testAfricaTimes() throws ParseException {
        waitForHttp(selenium).click(WORLDSCLOCK_LINK);
        waitForHttp(selenium).click(AFRICA_LINK);
        String tzInfo = selenium.getText(TIMEZONE_INFO);
        assertTrue("Text not found on the page",tzInfo.contains("Default Time Zone"));
        int from = tzInfo.indexOf("-") + 2;
        assertNotNull("The date should be parseable",formatter.parse(tzInfo, new ParsePosition(from)));
        assertTrue(FIRST_ENTRY_ERR, selenium.isTextPresent("Africa/Abidjan"));
        assertTrue(LAST_ENTRY_ERR, selenium.isTextPresent("Africa/Windhoek"));
        formatter.parse(selenium.getText(DATETIME_INFO));
    }

    @Test
    public void testAmericaTimes() throws ParseException {
        waitForHttp(selenium).click(WORLDSCLOCK_LINK);
        waitForHttp(selenium).click(AMERICA_LINK);
        assertTrue(FIRST_ENTRY_ERR, selenium.isTextPresent("America/Adak"));
        assertTrue(LAST_ENTRY_ERR, selenium.isTextPresent("America/Yellowknife"));
        formatter.parse(selenium.getText(DATETIME_INFO));
    }

    @Test
    public void testAsiaTimes() throws ParseException {
        waitForHttp(selenium).click(WORLDSCLOCK_LINK);
        waitForHttp(selenium).click(ASIA_LINK);
        assertTrue(FIRST_ENTRY_ERR, selenium.isTextPresent("Asia/Aden"));
        assertTrue(LAST_ENTRY_ERR, selenium.isTextPresent("Asia/Yerevan"));
        formatter.parse(selenium.getText(DATETIME_INFO));
    }

    @Test
    public void testAtlanticTimes() throws ParseException {
        waitForHttp(selenium).click(WORLDSCLOCK_LINK);
        waitForHttp(selenium).click(ATLANTIC_LINK);
        assertTrue(FIRST_ENTRY_ERR, selenium.isTextPresent("Atlantic/Azores"));
        assertTrue(LAST_ENTRY_ERR, selenium.isTextPresent("Atlantic/Stanley"));
        formatter.parse(selenium.getText(DATETIME_INFO));
    }

    @Test
    public void testAustraliaTimes() throws ParseException {
        waitForHttp(selenium).click(WORLDSCLOCK_LINK);
        waitForHttp(selenium).click(AUSTRALIA_LINK);
        assertTrue(FIRST_ENTRY_ERR, selenium.isTextPresent("Australia/ACT"));
        assertTrue(LAST_ENTRY_ERR, selenium.isTextPresent("Australia/Yancowinna"));
        formatter.parse(selenium.getText(DATETIME_INFO));
    }

    @Test
    public void testEuropeTimes() throws ParseException {
        waitForHttp(selenium).click(WORLDSCLOCK_LINK);
        waitForHttp(selenium).click(EUROPE_LINK);
        assertTrue(FIRST_ENTRY_ERR, selenium.isTextPresent("Europe/Amsterdam"));
        assertTrue(LAST_ENTRY_ERR, selenium.isTextPresent("Europe/Zurich"));
        formatter.parse(selenium.getText(DATETIME_INFO));
    }

    @Test
    public void testPacificTimes() throws ParseException {
        waitForHttp(selenium).click(WORLDSCLOCK_LINK);
        waitForHttp(selenium).click(PACIFIC_LINK);
        assertTrue(FIRST_ENTRY_ERR, selenium.isTextPresent("Pacific/Apia"));
        assertTrue(LAST_ENTRY_ERR, selenium.isTextPresent("Pacific/Yap"));
        formatter.parse(selenium.getText(DATETIME_INFO));
    }
    
    @Test
    public void testAvailableLocales() {
               
        assertTrue("Option '" + EN_LOCALE_LABEL_EN + "' not present.", selenium.isTextPresent(EN_LOCALE_LABEL_EN));
        assertTrue("Option '" + EN_LOCALE_LABEL_PT_BR + "' not present.", selenium.isTextPresent(EN_LOCALE_LABEL_PT_BR));
        
        // Change the locale to PT_BR
        waitForXhr(selenium).select(LOCALE_SELECT, optionLabel(EN_LOCALE_LABEL_PT_BR));
        assertTrue("Message '" + LOCALE_CHANGE_PT_BR + "' not present.", selenium.isTextPresent(LOCALE_CHANGE_PT_BR));
        
        assertTrue("Option '" + PT_BR_LOCALE_LABEL_EN + "' not present.", selenium.isTextPresent(PT_BR_LOCALE_LABEL_EN));
        assertTrue("Option '" + PT_BR_LOCALE_LABEL_PT_BR + "' not present.", selenium.isTextPresent(PT_BR_LOCALE_LABEL_PT_BR));
        
        // Page info should be in Portuguese now
        assertTrue("The page should contain a Portuguese description of the application", selenium.isTextPresent(PT_BR_INFO));
        
        waitForXhr(selenium).select(LOCALE_SELECT, optionLabel(PT_BR_LOCALE_LABEL_EN));
        assertTrue("Message '" + LOCALE_CHANGE_EN + "' not present.", selenium.isTextPresent(LOCALE_CHANGE_EN));
        
        // Page info should be back in English now
        assertTrue("The page should contain an English description of the application", selenium.isTextPresent(EN_INFO));
    }
}
