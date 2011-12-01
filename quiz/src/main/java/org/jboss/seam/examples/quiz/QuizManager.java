package org.jboss.seam.examples.quiz;

import java.io.Serializable;

/**
 * Managing quiz instances, which are configured by XML.
 * @author <a href="mailto:tremes@redhat.com"> Tomas Remes </a>
 *
 */
public class QuizManager implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -7683924919459973725L;

    private HistoricQuiz historicQuiz;
    
    private Quiz geographicQuiz;

    public HistoricQuiz getHistoricQuiz() {
        return historicQuiz;
    }

    public void setHistoricQuiz(HistoricQuiz historicQuiz) {
        this.historicQuiz = historicQuiz;
    }

    public Quiz getGeographicQuiz() {
        return geographicQuiz;
    }

    public void setGeographicQuiz(Quiz geographicQuiz) {
        this.geographicQuiz = geographicQuiz;
    }
      
      
}
