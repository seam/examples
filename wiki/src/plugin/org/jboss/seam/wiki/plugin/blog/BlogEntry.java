/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.blog;

import org.jboss.seam.wiki.core.model.WikiDocument;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Christian Bauer
 */
public class BlogEntry implements Serializable {

    WikiDocument entryDocument;
    Long commentCount;
    List<String> tags;

    public BlogEntry() {}

    public BlogEntry(WikiDocument entryDocument) {
        this.entryDocument = entryDocument;
    }

    public BlogEntry(WikiDocument entryDocument, Long commentCount) {
        this.entryDocument = entryDocument;
        this.commentCount = commentCount;
    }

    public WikiDocument getEntryDocument() {
        return entryDocument;
    }

    public void setEntryDocument(WikiDocument entryDocument) {
        this.entryDocument = entryDocument;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public List<String> getTagsAsList() {
        if (tags == null) tags = new ArrayList<String>(entryDocument.getTags());
        return tags;
    }

    public String toString() {
        return "BlogEntry: " + entryDocument + " Comments: " + commentCount;
    }
}
