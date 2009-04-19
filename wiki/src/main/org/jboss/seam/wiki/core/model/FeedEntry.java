/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "FEEDENTRY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "FEEDENTRY_TYPE", length = 255)
@DiscriminatorValue("EXTERNAL")
/*
TODO: This implementation of Comparable is not consistent with equals()!
 */
public class FeedEntry implements Serializable, Comparable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "FEEDENTRY_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    protected int version = 0;

    @Column(name = "LINK", nullable = false)
    @Length(min = 3, max = 1023)
    private String link;

    @Column(name = "TITLE", nullable = false)
    @Length(min = 3, max = 1023)
    private String title;

    @Transient
    private String titlePrefix = "";

    @Transient
    private String titleSuffix = "";

    @Column(name = "AUTHOR", nullable = false)
    @Length(min = 3, max = 255)
    private String author;

    @Column(name = "PUBLISHED_ON", nullable = false)
    private Date publishedDate = new Date();

    @Column(name = "UPDATED_ON", nullable = false)
    private Date updatedDate;

    @Column(name = "DESCRIPTION_TYPE", nullable = false)
    @Length(min = 3, max = 255)
    private String descriptionType;

    @Column(name = "DESCRIPTION_VALUE", nullable = false)
    @Length(min = 1, max = 131072) // TODO: We don't really know how much we need... so let's go for 128kb
    private String descriptionValue;

    public FeedEntry() {}

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }

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

    public String getTitlePrefix() {
        return titlePrefix;
    }

    public void setTitlePrefix(String titlePrefix) {
        this.titlePrefix = titlePrefix;
    }

    public String getTitleSuffix() {
        return titleSuffix;
    }

    public void setTitleSuffix(String titleSuffix) {
        this.titleSuffix = titleSuffix;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getDescriptionType() {
        return descriptionType;
    }

    public void setDescriptionType(String descriptionType) {
        this.descriptionType = descriptionType;
    }

    public String getDescriptionValue() {
        return descriptionValue;
    }

    public void setDescriptionValue(String descriptionValue) {
        this.descriptionValue = descriptionValue;
    }

    public int getReadAccessLevel() {
        return 0; // No restrictions
    }

    public boolean isTagged(String tag) {
        return false; // Can't be tagged
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

    // Sort by date
    public int compareTo(Object o) {
        FeedEntry other = (FeedEntry)o;
        if (getPublishedDate().getTime() > other.getPublishedDate().getTime()) return -1;
        return (getPublishedDate().getTime() == other.getPublishedDate().getTime() ? 0 : 1);
    }

    public String toString() {
        return "FeedEntry (" + getId() + ")";
    }

}
