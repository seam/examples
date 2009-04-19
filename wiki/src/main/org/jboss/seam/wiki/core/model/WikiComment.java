package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.jboss.seam.wiki.core.nestedset.NestedSetNode;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeInfo;
import org.jboss.seam.wiki.core.search.annotations.Searchable;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;
import org.jboss.seam.wiki.core.search.annotations.CompositeSearchables;
import org.jboss.seam.wiki.core.search.annotations.CompositeSearchable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "WIKI_COMMENT")
@org.hibernate.annotations.ForeignKey(name = "FK_WIKI_COMMENT_NODE_ID")

@org.hibernate.search.annotations.Indexed
@Searchable(description = "Comments")
@CompositeSearchables(
    @CompositeSearchable(
        description = "Content", type = SearchableType.PHRASE,
        properties = {"subject", "content"}
    )
)
public class WikiComment extends WikiNode<WikiComment> implements NestedSetNode<WikiComment>, Serializable {

    @Column(name = "SUBJECT", nullable = false)
    @Length(min = 3, max = 255)
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String subject;

    @Column(name = "FROM_USER_NAME", nullable = true)
    @Length(min = 3, max = 100)
    private String fromUserName;

    @Column(name = "FROM_USER_EMAIL", nullable = true)
    @Length(min = 0, max = 255)
    @Email
    private String fromUserEmail;

    @Column(name = "FROM_USER_HOMEPAGE", nullable = true)
    @Length(min = 0, max = 1023)
    private String fromUserHomepage;

    @Column(name = "CONTENT", nullable = false)
    @Length(min = 1, max = 32767)
    @Basic(fetch = FetchType.LAZY) // Lazy loaded through bytecode instrumentation
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String content;

    @Column(name = "USE_WIKI_TEXT", nullable = false)
    private boolean useWikiText = true;

    @Transient
    private Long level;

    @Embedded
    private NestedSetNodeInfo<WikiComment> nodeInfo;

    public WikiComment () {
        nodeInfo = new NestedSetNodeInfo<WikiComment>(this);
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public String getFromUserEmail() { return fromUserEmail; }
    public void setFromUserEmail(String fromUserEmail) { this.fromUserEmail = fromUserEmail; }

    public String getFromUserHomepage() { return fromUserHomepage;}
    public void setFromUserHomepage(String fromUserHomepage) { this.fromUserHomepage = fromUserHomepage; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isUseWikiText() { return useWikiText; }
    public void setUseWikiText(boolean useWikiText) { this.useWikiText = useWikiText; }

    public Long getLevel() { return level; }
    public void setLevel(Long level) { this.level = level; }

    public void flatCopy(WikiComment original, boolean copyLazyProperties) {
        super.flatCopy(original, copyLazyProperties);
        this.subject = original.subject;
        this.fromUserName = original.fromUserName;
        this.fromUserEmail = original.fromUserEmail;
        this.fromUserHomepage = original.fromUserHomepage;
        this.useWikiText = original.useWikiText;
        if (copyLazyProperties) {
            this.content = original.content;
        }
    }

    public WikiComment duplicate(boolean copyLazyProperties) {
        WikiComment dupe = new WikiComment();
        dupe.flatCopy(this, copyLazyProperties);
        return dupe;
    }

    public NestedSetNodeInfo<WikiComment> getNodeInfo() {
        return nodeInfo;
    }

    public NestedSetNodeInfo<WikiComment> getParentNodeInfo() {
        if (getParent() != null && WikiComment.class.isAssignableFrom(getParent().getClass()))
            return ((WikiComment)getParent()).getNodeInfo();
        return null;
    }

    public String[] getPropertiesForGroupingInQueries() {
        return new String[]{
            "version", "parent", "rating",
            "areaNumber", "name", "wikiname", "createdBy", "createdOn", "messageId",
            "lastModifiedBy", "lastModifiedOn", "readAccessLevel", "writeAccessLevel", "writeProtected",
            "subject", "fromUserName", "fromUserEmail", "fromUserHomepage", "useWikiText",
        };
    }

    public String[] getLazyPropertiesForGroupingInQueries() {
        return new String[] {"content"};
    }


    public String getPermURL(String suffix) {
        return getParentDocument().getId() + suffix + "#comment" + getId();
    }

    public String getWikiURL() {
        return getArea().getWikiname() + "/" + getParentDocument().getWikiname() + "#comment" + getId();
    }

    // TODO: Everything can have comments, this has the wrong name and it's crude
    public WikiNode getParentDocument() {
        WikiNode current = this.getParent();
        if (WikiDocument.class.isAssignableFrom(current.getClass())) return current;
        while (current.getParent() != null && WikiComment.class.isAssignableFrom(current.getParent().getClass())) {
            current = current.getParent();
        }
        return current.getParent(); // Let's just assume that the parent of a comment is at some point a document

    }

    public void setDerivedName(WikiNode node) {
        setName(node.getName() + ".Comment" + new Date().getTime());
    }

    public String toString() {
        return  "Comment (" + getId() + ")," +
                " Level: " + getLevel() +
                " Subject: '" + getSubject() + "'";
    }

}
