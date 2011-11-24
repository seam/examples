package org.jboss.seam.examples.quiz.ftest;


import static org.jboss.arquillian.ajocado.Ajocado.waitForHttp;
import static org.jboss.arquillian.ajocado.locator.LocatorFactory.xp;
import junit.framework.Assert;

import org.jboss.arquillian.ajocado.locator.XPathLocator;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Functional test for historic quiz. 
 *  @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 *
 */
@RunWith(Arquillian.class)
public class HistoricQuizTest extends AbstractQuizTest {
    
    private static final XPathLocator QUIZ_LINK = xp("//a[contains(@name,'historicQuiz')]");
    private static final String FIRST_QUESTION = "1) What is the latest state to join the United States, bringing the number to 50?";
    private static final String SECOND_QUESTION = "2) John F. Kennedy was assassinated in ";
    private static final String THIRD_QUESTION = "3) What was the first capital of ancient Egypt?";
    
    @Before
    public void start() {
        selenium.setSpeed(300);
        selenium.open(contextPath);
        waitForHttp(selenium).click(QUIZ_LINK);
        Assert.assertTrue("Cannot obtain firt question!!!",selenium.isTextPresent(FIRST_QUESTION));
    }

    @Test
    public void testRightHistoricAnswers() {

        answerQuestion(ANSWER1, true);
        Assert.assertTrue("Cannot obtain second question!!!",selenium.isTextPresent(SECOND_QUESTION));

        answerQuestion(ANSWER1, true);
        Assert.assertTrue("Cannot obtain third question!!!",selenium.isTextPresent(THIRD_QUESTION));

        answerQuestion(ANSWER3, true);
        Assert.assertTrue("Quiz is not finished!",selenium.isTextPresent(QUIZ_FINISHED_WITH_FULL_SCORE));
        waitForHttp(selenium).click(HOME_LINK);
    }

    @Test
    public void testNoHistoricAnswers() {
        waitForHttp(selenium).click(NEXT_BUTTON);
        waitForHttp(selenium).click(NEXT_BUTTON);
        waitForHttp(selenium).click(NEXT_BUTTON);
        Assert.assertTrue( "Quiz is not finished!",selenium.isTextPresent(QUIZ_FINISHED_WITH_ZERO_SCORE));
        waitForHttp(selenium).click(HOME_LINK);
    }

    @Test
    public void testWrongHistoricAnswers() {
        
        answerQuestion(ANSWER2, false);
        Assert.assertTrue("Cannot obtain second question!!!", selenium.isTextPresent(SECOND_QUESTION));

        answerQuestion(ANSWER2, false);
        Assert.assertTrue("Cannot obtain third question!!!",selenium.isTextPresent(THIRD_QUESTION));

        answerQuestion(ANSWER1, false);
        Assert.assertTrue("Quiz is not finished!",selenium.isTextPresent(QUIZ_FINISHED_WITH_ZERO_SCORE));
        waitForHttp(selenium).click(HOME_LINK);
    }
}
