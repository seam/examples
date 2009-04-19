package org.jboss.seam.wiki.core.search.metamodel;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.search.PropertySearch;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;

import java.io.Serializable;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * Meta-information about a logical searchable property.
 * <p>
 * Generalized building of Lucene queries, called by subclasses.
 * <p>
 * TODO: Implement NUMRANGE query building
 *
 * @author Christian Bauer
 */
public abstract class SearchableProperty implements Serializable, Comparable {

    public static final String TERM_INCLUDE             = "include";
    public static final String TERM_EXCLUDE             = "exclude";
    public static final String TERM_MATCHEXACTPHRASE    = "matchExactPhrase";
    public static final String TERM_NUMOFDAYS           = "numOfDays";

    protected Log log = org.jboss.seam.log.Logging.getLog(getClass());

    private String description;
    private SearchableType type;

    public SearchableProperty() {}

    public SearchableProperty(String description, SearchableType type) {
        this.description = description;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SearchableType getType() {
        return type;
    }

    public void setType(SearchableType type) {
        this.type = type;
    }

    public int compareTo(Object o) {
        return getDescription().compareTo( ((SearchableProperty)o).getDescription() );
    }

    public abstract Query getQuery(PropertySearch search);

    protected Query buildIncludeQuery(String fieldName, PropertySearch search) {
        Query query = null;

        if (getType().equals(SearchableType.PHRASE)) {

            String includeString = (String)search.getTerms().get(TERM_INCLUDE);
            Boolean matchExactPhrase = (Boolean)search.getTerms().get(TERM_MATCHEXACTPHRASE);
            if (includeString != null && includeString.length() >0) {

                if(matchExactPhrase != null && matchExactPhrase) {
                    log.debug("building include phrase query for field: " + fieldName);
                    query = buildPhraseQuery(fieldName, includeString);
                } else {
                    log.debug("building include term query for field: " + fieldName);
                    query = buildTermQuery(fieldName, includeString);
                }

            }

        } else if (getType().equals(SearchableType.PASTDATE)) {

            String numOfDays = (String)search.getTerms().get(TERM_NUMOFDAYS);
            if (numOfDays != null &&  numOfDays.length() >0) {
                log.debug("building past date query for field: " + fieldName);

                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                Calendar today = new GregorianCalendar();
                Calendar startDate = new GregorianCalendar();
                startDate.add(Calendar.DAY_OF_YEAR, -Integer.valueOf(numOfDays));

                log.debug("date range query start: " + df.format(startDate.getTime()) );
                log.debug("date range query end: " + df.format(today.getTime()) );

                query = buildRangeQuery(fieldName, df.format(startDate.getTime()), df.format(today.getTime()));
            }
        } else if (getType().equals(SearchableType.STRING)) {

            String includeString = (String)search.getTerms().get(TERM_INCLUDE);
            if (includeString != null && includeString.length() >0) {
                log.debug("building include term query for field: " + fieldName);
                query = buildTermQuery(fieldName, includeString);
            }
        }


        return query;
    }

    protected Query buildExcludeQuery(String fieldName, PropertySearch search) {
        Query query = null;

        if (getType().equals(SearchableType.PHRASE)) {
            log.debug("building exclude phrase query for field: " + fieldName);

            String includeString = (String)search.getTerms().get(TERM_INCLUDE);
            String excludeString = (String)search.getTerms().get(TERM_EXCLUDE);
            Boolean matchExactPhrase = (Boolean)search.getTerms().get(TERM_MATCHEXACTPHRASE);
            if (includeString != null && includeString.length() >0 && excludeString != null && excludeString.length() > 0) {

                if(matchExactPhrase != null && matchExactPhrase) {
                    log.debug("building exclude phrase query for field: " + fieldName);
                    query = buildPhraseQuery(fieldName, includeString);
                } else {
                    log.debug("building exclude term query for field: " + fieldName);
                    query = buildTermQuery(fieldName, excludeString);
                }

            }
        }
        return query;

    }

    private Query buildPhraseQuery(String fieldName, String terms) {
        try {
            PhraseQuery query = new PhraseQuery();
            query.setSlop(0);

            TokenStream includeStream =
                    new StandardAnalyzer().tokenStream(null, new StringReader(escape(terms).toLowerCase()));

            while (true) {
                Token t = includeStream.next();
                if (t == null) break;
                query.add( new Term(fieldName, t.termText()) );
            }

            return query.getTerms().length > 0 ? query : null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Query buildTermQuery(String fieldName, String terms) {
        String[] termsArray = escape(terms).toLowerCase().split("\\s"); // Just split user input by whitespace
        if (termsArray.length > 1) {
            BooleanQuery query = new BooleanQuery();
            for (String s: termsArray) {
                TermQuery termQuery = new TermQuery(new Term(fieldName, s) );
                query.add(termQuery, BooleanClause.Occur.SHOULD);
            }
            return query.getClauses().length > 0 ? query : null;
        } else {
            return termsArray.length != 0 ? new TermQuery(new Term(fieldName, termsArray[0])) : null;
        }
    }

    private Query buildRangeQuery(String fieldName, String begin, String end) {
        return new ConstantScoreRangeQuery(fieldName, begin, end, true, true); // Inclusive of begin and end
    }


    private String escape(String userInput) {
        userInput = userInput.replaceAll(Pattern.quote("("), "\\(");
        userInput = userInput.replaceAll(Pattern.quote(")"), "\\)");
        return userInput;
    }


}
