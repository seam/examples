package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "WIKI_MENU_ITEM")
public class WikiMenuItem implements Serializable {

    @Id
    @Column(name = "DIRECTORY_ID")
    private Long directoryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DIRECTORY_ID", insertable = false, updatable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_MENU_ITEM_DIRECTORY_ID")
    //TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private WikiDirectory directory;

    @Column(name = "DISPLAY_POSITION", nullable = false)
    private long displayPosition;

    protected WikiMenuItem() {}

    public WikiMenuItem(WikiDirectory directory) {
        this.directoryId = directory.getId();
        setDirectory(directory);
    }

    public Long getDirectoryId() {
        return directoryId;
    }

    public void setDirectoryId(Long directoryId) {
        this.directoryId = directoryId;
    }

    public WikiDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(WikiDirectory directory) {
        this.directory = directory;
    }

    public long getDisplayPosition() {
        return displayPosition;
    }

    public void setDisplayPosition(long displayPosition) {
        this.displayPosition = displayPosition;
    }

    public String toString() {
        return "Menu Item position " + getDisplayPosition() + ": " + getDirectory();
    }
}
