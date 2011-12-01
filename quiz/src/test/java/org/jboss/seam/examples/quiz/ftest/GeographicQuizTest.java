package org.jboss.seam.examples.quiz.ftest;

import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.jboss.arquillian.ajocado.locator.JQueryLocator;
import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Functional test for geographic quiz.
 * 
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 * 
 */
@RunWith(Arquillian.class)
public class GeographicQuizTest extends AbstractQuizTest {

    private static final XPathLocator QUIZ_LINK = xp("//a[contains(@name,'geoQuiz')]");
    private static final String FIRST_QUESTION = "1) What is the longest river in the world?";
    private static final String SECOND_QUESTION = "2) What country has the largest population in the world?";
    private static final String THIRD_QUESTION = "3) What is the largest country in the world?";
    
    @Before
    public void start() {
        selenium.setSpeed(300);
        selenium.open(contextPath);
        waitForHttp(selenium).click(QUIZ_LINK);
        Assert.assertTrue("Cannot obtain first question!!!", selenium.isTextPresent(FIRST_QUESTION));

    }

    @Test
    public void testRightGeographicAnswers() {

        // answering first question
        answerQuestion(ANSWER1, true);
        Assert.assertTrue("Cannot obtain second question!!!", selenium.isTextPresent(SECOND_QUESTION));

        // answering second question
        answerQuestion(ANSWER2, true);
        Assert.assertTrue("Cannot obtain third question!!!", selenium.isTextPresent(THIRD_QUESTION));

        // answering third question
        answerQuestion(ANSWER3, true);
        Assert.assertTrue("Quiz is not finished!", selenium.isTextPresent(QUIZ_FINISHED_WITH_FULL_SCORE));
        waitForHttp(selenium).click(HOME_LINK);
    }

    @Test
    public void testNoGeographicAnswers() {
        waitForHttp(selenium).click(NEXT_BUTTON);
        waitForHttp(selenium).click(NEXT_BUTTON);
        waitForHttp(selenium).click(NEXT_BUTTON);
        Assert.assertTrue("Quiz is not finished!", selenium.isTextPresent(QUIZ_FINISHED_WITH_ZERO_SCORE));
        waitForHttp(selenium).click(HOME_LINK);
    }

    @Test
    public void testWrongGeographicAnswers() {

        // answering first question
        answerQuestion(ANSWER3, false);
        Assert.assertTrue("Cannot obtain second question!!!", selenium.isTextPresent(SECOND_QUESTION));

        // answering second question
        answerQuestion(ANSWER1, false);
        Assert.assertTrue("Cannot obtain third question!!!", selenium.isTextPresent(THIRD_QUESTION));

        // answering third question
        answerQuestion(ANSWER2, false);
        Assert.assertTrue(selenium.isTextPresent(QUIZ_FINISHED_WITH_ZERO_SCORE));
        waitForHttp(selenium).click(HOME_LINK);
    }
    
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
