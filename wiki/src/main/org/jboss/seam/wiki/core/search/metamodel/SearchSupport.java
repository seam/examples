/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.search.metamodel;

import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.util.WikiUtil;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.Query;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.util.Set;
import java.io.StringReader;

/**
 * Superclass for search support, extend it to add search options to the wiki.
 * <p>
 * Extend this class and return <tt>SearchableEntityHandler</tt> instances for each
 * entity you want to be able to search in the user interface. The handlers need to
 * be able to extract a <tt>SearchHit</tt> from a given query and the original entity
 * instance. This <tt>SearchHit</tt> is then displayed. If you have a string-based
 * property and you want to simply show the "best" fragments of a hit, use the
 * <tt>escapeBestFragments()</tt> convenience method.
 * <p>
 * Note that you also need to annotate any entity class and its properties
 * with <tt>@Searchable</tt>.
 *
 * @see org.jboss.seam.wiki.core.search.annotations.Searchable
 * @see SearchableEntityHandler
 * @see org.jboss.seam.wiki.core.search.SearchHit
 *
 * @author Christian Bauer
 */
@Scope(ScopeType.APPLICATION)
public abstract class SearchSupport {

    private static final String INTERNAL_BEGIN_HIT = "!!!BEGIN_HIT!!!";
    private static final String INTERNAL_END_HIT = "!!!END_HIT!!!";

    @Observer("Search.addSearchSupport")
    public void add(Set<SearchSupport> searchSupportComponents) {
        searchSupportComponents.add(this);
    }

    /**
     * Returns the hits of the given query as fragments, highlighted, concatenated, and separated.
     * <p>
     * Pass in a <tt>NullFragmenter</tt> if you don't want any fragmentation by terms but
     * simply the hits highlighted. Otherwise, you will most likely use <tt>SimpleFragmenter</tt>.
     * The text you supply must be the same that was indexed, it will go through the same
     * analysis procedure to find the hits. Do not pass a different String than the one indexed
     * by Hibernate Search! If you use transparent string bridge with Hibernate Search, run the
     * bridge before passing the string into this method.
     * <p>
     * This method escapes any dangerous HTML characters in the indexed text and fragments by
     * replacing it with HTML entities. You can use the returned string directly to build a
     * <tt>SearchHit</tt>.
     *
     * @param query the query that produced hits
     * @param fragmenter a fragmenter that can split the indexed text
     * @param indexedText the original text that was analyzed and indexed by Hibernate Search (after any bridges!)
     * @param numOfFragments the number of fragments to include in the returned result
     * @param alternativeLength if there are no hits to highlight, how many characters of the original text to return
     * @return the fragmented, highglighted, and then concatenated substring of the indexed text
     */
    protected String escapeBestFragments(Query query, Fragmenter fragmenter,
                                         String indexedText, int numOfFragments, int alternativeLength) {

        // The HTML escaping forces us to first fragment with internal placeholders...
        Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter(INTERNAL_BEGIN_HIT, INTERNAL_END_HIT), new QueryScorer(query));
        highlighter.setTextFragmenter(fragmenter);
        try {
            // Use the same analyzer as the indexer!
            TokenStream tokenStream = new StandardAnalyzer().tokenStream(null, new StringReader(indexedText));

            String unescapedFragements =
                    highlighter.getBestFragments(tokenStream, indexedText, numOfFragments, getFragmentSeparator());

            String escapedFragments = WikiUtil.escapeHtml(WikiUtil.removeMacros(unescapedFragements), false, false);

            // .. and then replace the internal placeholders with real tags after HTML has been escaped
            escapedFragments = escapedFragments.replaceAll(INTERNAL_BEGIN_HIT, getBeginHitTag());
            escapedFragments = escapedFragments.replaceAll(INTERNAL_END_HIT, getEndHitTag());

            // Strip out macros

            // If no fragments were produced (no hits), return the original text as an alternative
            if (escapedFragments.length() == 0 && alternativeLength != 0) {
                return WikiUtil.escapeHtml(
                        WikiUtil.removeMacros(
                            indexedText.substring(
                                0,
                                indexedText.length()>alternativeLength ? alternativeLength : indexedText.length()
                            )
                        ), false, false
                       );
            } else if (escapedFragments.length() == 0 && alternativeLength == 0){
                return WikiUtil.escapeHtml(WikiUtil.removeMacros(indexedText), false, false);
            }

            return escapedFragments;

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * String used to mark the beginning of a fragment.
     * <p>
     * Defaults to &lt:b&gt;, can be overriden by subclass.
     *
     * @return String used to mark the beginning of a fragment.
     */
    protected String getBeginHitTag() {
        return "<b>";
    }

    /**
     * String used to mark the end of a fragment.
     * <p>
     * Defaults to &lt:/b&gt;, can be overriden by subclass.
     *
     * @return String used to mark the end of a fragment.
     */
    protected String getEndHitTag() {
        return "</b>";
    }

    /**
     * Separator string between two fragments.
     * <p>
     * Defaults to <tt>... ...</tt> (just dots with a space).
     *
     * @return Separator string between two fragments.
     */
    protected String getFragmentSeparator() {
        return "... ...";
    }

    /**
     * Create and return any <tt>SearchableEntityHandler</tt> you require search functionality for.
     * <p>
     * This is called on startup only by the internal registry, to assemble all handlers.
     *
     * @return SearchableEntityHandler typed for a particular indexed/searchable entity class
     */
    public abstract Set<SearchableEntityHandler> getSearchableEntityHandlers();

}
