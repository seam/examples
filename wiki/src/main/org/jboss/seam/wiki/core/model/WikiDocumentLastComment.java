package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.util.Date;

/**
 * A not-so-pretty denormalization and duplication of data, so aggregation queries
 * can execute faster on nested set trees.
 *
 * @author Christian Bauer
 */
@Entity
@Table(
    name = "WIKI_DOCUMENT_LAST_COMMENT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"WIKI_DOCUMENT_ID", "LAST_COMMENT_ID"})
    }
)

public class WikiDocumentLastComment {

    @Id
    @Column(name = "WIKI_DOCUMENT_ID", nullable = false)
    private Long documentId;

    @Column(name = "LAST_COMMENT_ID", nullable = false)
    protected Long lastCommentId;

    @Column(name = "LAST_COMMENT_CREATED_ON", nullable = false)
    protected Date lastCommentCreatedOn;

    public WikiDocumentLastComment() {}

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getLastCommentId() {
        return lastCommentId;
    }

    public void setLastCommentId(Long lastCommentId) {
        this.lastCommentId = lastCommentId;
    }

    public Date getLastCommentCreatedOn() {
        return lastCommentCreatedOn;
    }

    public void setLastCommentCreatedOn(Date lastCommentCreatedOn) {
        this.lastCommentCreatedOn = lastCommentCreatedOn;
    }
}
