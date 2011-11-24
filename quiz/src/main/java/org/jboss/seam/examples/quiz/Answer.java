package org.jboss.seam.examples.quiz;

import java.io.Serializable;

/**
 * Representation of quiz answer configured by XML
 * 
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 * 
 */
public class Answer implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4175021231494836019L;
    private String answerText;
    private int id;
    private boolean isCorrect;

    /**
     * 
     * @return answer id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @return answer text
     */
    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    /**
     * 
     * @return true if current answer is right
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
