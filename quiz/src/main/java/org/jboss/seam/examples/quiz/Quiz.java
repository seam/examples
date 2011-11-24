package org.jboss.seam.examples.quiz;

/**
 * Quiz interface defines key methods.
 * 
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 * 
 */
public interface Quiz {

    /**
     * Save action resolves question answer. 
     */
    public void saveQuestion();

    /**
     * 
     * @return actual score
     */
    public int getScore();
    
    /**
     * Next question action.
     */
    public void nextQuestion();
    
    /**
     * Resets score, questions and answers for new quiz.
     */
    public void resetQuiz();

    /**
     * 
     * @return actual question
     */
    public Question getActualQuestion();

}
