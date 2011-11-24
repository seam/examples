package org.jboss.seam.examples.quiz;

import java.io.Serializable;

import javax.enterprise.inject.Instance;

/**
 * Representation of quiz question configured by XML
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 *
 */
public class Question implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 3063381809681190828L;
    private String questionText;
    private Instance<Answer> answers;
    private int id;
    private boolean saved=false;
  
    /**
     * 
     * @return true if answer has been already saved
     */
    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    
    /**
     * 
     * @return question id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * 
     * @return answers for current question
     */
    public Instance<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(Instance<Answer> answers) {
        this.answers = answers;
        
    }
    
    /**
     * 
     * @return question text
     */
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
     
    
}
