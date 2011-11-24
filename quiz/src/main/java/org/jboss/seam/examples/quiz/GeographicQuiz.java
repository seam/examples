package org.jboss.seam.examples.quiz;

import java.io.Serializable;
import java.util.List;
import org.jboss.seam.international.status.Messages;

/**
 * Representation of geographic quiz configured by XML
 * 
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 * 
 */
public class GeographicQuiz implements Quiz, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5627554823035070515L;
    private List<Question> questions;
    private int score = 0;
    //getting zeroth element from questions list
    private int questionNumber = 0;
    private Integer selectedAnswer;
    private Messages messages;

    public void setScore(int score) {
        this.score = score;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public boolean isNextQuestionAvailable() {
        return questionNumber != questions.size();
    }

    public Question getActualQuestion() {
        if (isNextQuestionAvailable()) {
            return getQuestions().get(questionNumber);
        } else {
            return null;
        }
    }

    public Integer getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(Integer selectedAnswer) {
        if(selectedAnswer != null){
            this.selectedAnswer = selectedAnswer;
        }else{
            this.selectedAnswer = 0;
        }
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public int getScore() {

        return score;
    }

    public void saveQuestion() {

        Answer answer = null;
        for (Answer a : getActualQuestion().getAnswers()) {
            if (a.getId() == selectedAnswer) {
                answer = a;
            }
        }
        if (answer != null && answer.isCorrect()) {
            messages.info("Excellent! You're right!");
            score = score + 10;
        } else
            messages.info("Wrong answer!");
        getActualQuestion().setSaved(true);
        setSelectedAnswer(0);

    }

    public void nextQuestion() {
        questionNumber++;
        setSelectedAnswer(0);
    }

    public void resetQuiz() {
        questionNumber = 0;
        score = 0;
        selectedAnswer = 0;
        for(Question q : questions){
            q.setSaved(false);
        }
         
    }

}
