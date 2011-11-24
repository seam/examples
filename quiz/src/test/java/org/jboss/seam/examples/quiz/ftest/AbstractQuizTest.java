package org.jboss.seam.examples.quiz.ftest;


import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.jq;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;

import org.jboss.arquillian.ajocado.framework.AjaxSelenium;
import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;


/**
 * Utility methods for quiz example
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 *
 */
public class AbstractQuizTest {
    
    public static final String CORRECT_ANSWER_MESSAGE = "Excellent! You're right!";
    public static final String WRONG_ANSWER_MESSAGE = "Wrong answer!";
    public static final String QUIZ_FINISHED_WITH_FULL_SCORE = "Quiz is finished. Your score is 30 points.";
    public static final String QUIZ_FINISHED_WITH_ZERO_SCORE = "Quiz is finished. Your score is 0 points.";

    public static final JQueryLocator ANSWER1 = jq("[value='1']");
    public static final JQueryLocator ANSWER2 = jq("[value='2']");
    public static final JQueryLocator ANSWER3 = jq("[value='3']");
    public static final JQueryLocator HOME_LINK=jq("[id='linkForm:home']");
    public static final XPathLocator SAVE_BUTTON = xp("//input[contains(@id,'questionForm:save')]");
    public static final XPathLocator NEXT_BUTTON = xp("//input[contains(@id,'questionForm:next')]");
    public static final String ARCHIVE_NAME = "config-quiz.war";
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
     
       
    /**
     * Clicks on given input, saves answer and continues to next question.
     * @param answerInput
     * @param isCorrect
     */
    public void answerQuestion(JQueryLocator answerInput, boolean isCorrect) {
        selenium.click(answerInput);
        waitForHttp(selenium).click(SAVE_BUTTON);
        if (isCorrect) {
            assertTrue("This is not correct answer!",selenium.isTextPresent(CORRECT_ANSWER_MESSAGE));
        } else {
            assertTrue( "This is correct answer!",selenium.isTextPresent(WRONG_ANSWER_MESSAGE));
        }
        waitForHttp(selenium).click(NEXT_BUTTON);
    }
      
}
