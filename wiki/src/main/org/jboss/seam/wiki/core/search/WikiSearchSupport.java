package org.jboss.seam.wiki.core.search;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.search.metamodel.SearchSupport;
import org.jboss.seam.wiki.core.search.metamodel.SearchableEntityHandler;
import org.jboss.seam.wiki.core.ui.WikiURLRenderer;
import org.jboss.seam.Component;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;

import java.util.Set;
import java.util.HashSet;

/**
 * Handlers for searchable entities of the core domain model.
 *
 * @author Christian Bauer
 */
@Name("wikiSearchSupport")
public class WikiSearchSupport extends SearchSupport {

    public Set<SearchableEntityHandler> getSearchableEntityHandlers() {

        return new HashSet<SearchableEntityHandler>() {{

            add(
                new SearchableEntityHandler<WikiDocument>() {

                    public boolean isReadAccessChecked() {
                        return true;
                    }

                    public SearchHit extractHit(Query query, WikiDocument doc) throws Exception {
                        WikiURLRenderer urlRenderer = (WikiURLRenderer) Component.getInstance(WikiURLRenderer.class);
                        return new SearchHit(
                            WikiDocument.class.getSimpleName(),
                            "icon.doc.gif",
                            "(" + doc.getCreatedBy().getFullname() + ") " + escapeBestFragments(query, new NullFragmenter(), doc.getName(), 0, 0),
                            urlRenderer.renderURL(doc),
                            escapeBestFragments(query, new SimpleFragmenter(100), doc.getContent(), 5, 350)
                        );
                    }
                }
            );

            add(
                new SearchableEntityHandler<WikiComment>() {
                    public SearchHit extractHit(Query query, WikiComment comment) throws Exception {
                        WikiURLRenderer urlRenderer = (WikiURLRenderer)Component.getInstance(WikiURLRenderer.class);
                        return new SearchHit(
                            WikiComment.class.getSimpleName(),
                            "icon.user.gif",
                            "(" + (comment.getCreatedBy() != null ? comment.getCreatedBy().getFullname() : comment.getFromUserName()) + ") "
                                + escapeBestFragments(query, new NullFragmenter(), comment.getSubject(), 0, 0),
                            urlRenderer.renderURL(comment),
                            escapeBestFragments(query, new SimpleFragmenter(100), comment.getContent(), 5, 350)
                        );
                    }
                }
            );

        }};
    }

}
