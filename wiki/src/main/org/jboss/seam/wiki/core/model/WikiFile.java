package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.util.*;
import java.io.Serializable;

@Entity
@Table(name = "WIKI_FILE")
@org.hibernate.annotations.ForeignKey(name = "FK_WIKI_FILE_NODE_ID")
//TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
public abstract class WikiFile<N extends WikiFile> extends WikiNode<N> implements Serializable {

    @org.hibernate.annotations.CollectionOfElements(fetch = FetchType.LAZY)
    @JoinTable(name = "WIKI_TAG", joinColumns = @JoinColumn(name = "FILE_ID"))
    @Column(name = "TAG", nullable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_WIKI_TAG_FILE_ID")
    @org.hibernate.annotations.Sort(type = org.hibernate.annotations.SortType.NATURAL)
    //@org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @org.hibernate.annotations.BatchSize(size = 20)
    private SortedSet<String> tags = new TreeSet<String>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "WIKI_FILE_LINK",
        joinColumns = @JoinColumn(name = "SOURCE_WIKI_FILE_ID", nullable = false, updatable = false),
        inverseJoinColumns= @JoinColumn(name = "TARGET_WIKI_FILE_ID", nullable = false, updatable = false)
    )
    @org.hibernate.annotations.ForeignKey(name = "FK_SOURCE_WIKI_FILE_ID", inverseName = "FK_TARGET_WIKI_FILE_ID")
    @org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
    protected Set<WikiFile> outgoingLinks = new HashSet<WikiFile>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "outgoingLinks")
    @org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
    protected Set<WikiFile> incomingLinks = new HashSet<WikiFile>();

    // Uses Hibernates ability to map the same class twice, see HistoricalWikiFile.hbm.xml
    @Transient
    private Long historicalFileId;
    @Transient
    protected String lastModifiedByUsername;
    @Column(name = "FILE_REVISION", nullable = false)
    private int revision = 0;

    protected WikiFile() {}

    protected WikiFile(String name) {
        super(name);
    }

    public SortedSet<String> getTags() { return tags; }
    public void setTags(SortedSet<String> tags) { this.tags = tags; }

    public Set<WikiFile> getOutgoingLinks() { return outgoingLinks; }
    public void setOutgoingLinks(Set<WikiFile> outgoingLinks) { this.outgoingLinks = outgoingLinks; }

    public Set<WikiFile> getIncomingLinks() { return incomingLinks; }
    public void setIncomingLinks(Set<WikiFile> incomingLinks) { this.incomingLinks = incomingLinks; }

    public Long getHistoricalFileId() { return historicalFileId; }
    public String getLastModifiedByUsername() { return lastModifiedByUsername; }

    @Override
    public void setLastModifiedBy(User lastModifiedBy) {
        super.setLastModifiedBy(lastModifiedBy);
        if (lastModifiedBy != null)
            lastModifiedByUsername = lastModifiedBy.getUsername();
    }

    public int getRevision() { return revision; }
    public void setRevision(int revision) { this.revision = revision; }

    public void incrementRevision() {
        revision++;
    }

    public boolean isHistoricalRevision() {
        return historicalFileId!=null;
    }

    public void flatCopy(WikiFile original, boolean copyLazyProperties) {
        super.flatCopy(original, copyLazyProperties);
        this.revision = original.revision;
    }

    public void rollback(WikiFile revision) {
        this.name = revision.name;
    }

    public List<String> getTagsAsList() {
        return new ArrayList<String>(getTags());
    }

    public boolean isTagged(String tag) {
        return getTags().contains(tag);
    }

    /* TODO: Remove this at some point, when we are sure we don't need it anymore
    public String getTagsCommaSeparated() {
        if (getTags().size() == 0) return null;
        StringBuilder tagString = new StringBuilder();
        for (String s : getTags()) {
            tagString.append(s).append(", ");
        }
        return tagString.length()>0
                ? tagString.toString().substring(0, tagString.toString().length()-2) // Cut last comma
                : tagString.toString();
    }

    public void setTagsCommaSeparated(String tagString) {
        getTags().clear();
        if (tagString == null || tagString.length() == 0) return;
        String[] tagArray = tagString.split(",");
        for (String s : tagArray) {
            getTags().add(s.trim());
        }
    }
    */

    public abstract String getHistoricalEntityName();

    public abstract String getFeedDescription();
}
