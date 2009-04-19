/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.util.*;
import java.io.Serializable;

@Entity
@Table(name = "FEED")
@org.hibernate.annotations.BatchSize(size = 10)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "FEED_TYPE", length = 255)
@DiscriminatorValue("EXTERNAL")
public class Feed implements Serializable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "FEED_ID")
    private Long id;

    @Column(name = "FEED_LINK", nullable = false, length = 1023)
    private String link;

    @Column(name = "TITLE", nullable = false, length = 1023)
    private String title;

    @Column(name = "DESCRIPTION", nullable = true, length = 1023)
    private String description;

    @Column(name = "AUTHOR", nullable = false, length = 1023)
    private String author;

    @Column(name = "PUBLISHED_ON", nullable = false, updatable = false)
    private Date publishedDate = new Date();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "FEED_FEEDENTRY",
        joinColumns = @JoinColumn(name = "FEED_ID", nullable = false, updatable = false),
        inverseJoinColumns= @JoinColumn(name = "FEEDENTRY_ID", nullable = false, updatable = false)
    )
    @org.hibernate.annotations.ForeignKey(name = "FK_FEED_FEEDENTRY_FEED_ID", inverseName = "FK_FEED_FEEDENTRY_FEEDENTRY_ID")
    @org.hibernate.annotations.Sort(type = org.hibernate.annotations.SortType.NATURAL)
    private SortedSet<FeedEntry> feedEntries = new TreeSet<FeedEntry>();

    public Feed() { }

    // Immutable properties

    public Long getId() { return id; }

    // Mutable properties

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public SortedSet<FeedEntry> getFeedEntries() {
        return feedEntries;
    }

    public void setFeedEntries(SortedSet<FeedEntry> feedEntries) {
        this.feedEntries = feedEntries;
    }

    public int getReadAccessLevel() {
        return 0; // No restrictions
    }

    public String getURL() {
        return ""; // Depends...
    }

    // Need this for JSF EL expressions
    public boolean isInstance(String className) {
        try {
            Class clazz = Class.forName(getClass().getPackage().getName() + "." + className);
            return isInstance(clazz);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isInstance(Class clazz) {
        return clazz.isAssignableFrom(this.getClass());
    }

    public String toString() {
        return "Feed: " + getId();
    }
}
