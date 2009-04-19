package org.jboss.seam.wiki.core.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("WIKI_DOCUMENT")
public class WikiDocumentFeedEntry extends FeedEntry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WIKI_DOCUMENT_ID", nullable = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_FEEDENTRY_WIKI_DOCUMENT_ID")
    //TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private WikiDocument document;

    public WikiDocument getDocument() {
        return document;
    }

    public void setDocument(WikiDocument document) {
        this.document = document;
    }

    public int getReadAccessLevel() {
        return getDocument().getReadAccessLevel();
    }

    public boolean isTagged(String tag) {
        return getDocument().isTagged(tag);
    }
    
}
