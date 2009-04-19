package org.jboss.seam.wiki.core.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("WIKI_COMMENT")
public class WikiCommentFeedEntry extends FeedEntry {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WIKI_COMMENT_ID", nullable = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_FEEDENTRY_WIKI_COMMENT_ID")
    //TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private WikiComment comment;

    public WikiComment getComment() {
        return comment;
    }

    public void setComment(WikiComment comment) {
        this.comment = comment;
    }

    public int getReadAccessLevel() {
        return getComment().getReadAccessLevel();
    }

}
