package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Pattern;
import org.hibernate.validator.Length;
import org.hibernate.validator.Range;
import org.jboss.seam.wiki.core.search.annotations.Searchable;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;
import org.jboss.seam.wiki.core.search.PaddedIntegerBridge;

import javax.persistence.*;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
    name = "WIKI_NODE",
    uniqueConstraints = {
        // Wikiname of a document needs to be unique within an area
        @UniqueConstraint(columnNames = {"AREA_NR", "WIKINAME"})
    }
)
@org.hibernate.annotations.Filter(
    name = "accessLevelFilter",
    condition = "READ_ACCESS_LEVEL <= :currentAccessLevel"
)
@org.hibernate.annotations.BatchSize(size = 20)
public abstract class WikiNode<N extends WikiNode> implements Comparable {

    public static enum SortableProperty {
        name, createdOn, createdBy, lastModifiedOn, lastModifiedBy, rating
    }

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "NODE_ID")
    @org.hibernate.search.annotations.DocumentId(name = "nodeId")
    protected Long id;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    protected int version = 0;

    @Column(name = "AREA_NR", nullable = false)
    protected Long areaNumber;

    @Column(name = "NAME", length = 255, nullable = false)
    @Length(min = 3, max = 255)
    @Pattern(
        regex="^[a-zA-Z0-9]+[^/#\\|\\]\\[]*",
        message="#{messages['lacewiki.entity.NameMustStartWithLetterOrNumber']}"
    )
    protected String name;

    @Column(name = "WIKINAME", length = 255, nullable = false)
    @Pattern(
        regex="^[A-Z0-9]+.*",
        message="#{messages['lacewiki.entity.NameMustStartWithUppercaseLetterOrNumber']}"
    )
    protected String wikiname;

    @Column(name = "MESSAGE_ID", length = 1023, nullable = true)
    protected String messageId;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    @org.hibernate.search.annotations.Field(
        index = org.hibernate.search.annotations.Index.UN_TOKENIZED,
        store = org.hibernate.search.annotations.Store.YES
    )
    @org.hibernate.search.annotations.DateBridge(resolution = org.hibernate.search.annotations.Resolution.DAY)
    @Searchable(description = "Created", type = SearchableType.PASTDATE)
    protected Date createdOn = new Date();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_USER_ID", nullable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_WIKI_NODE_CREATED_BY_USER_ID")
    protected User createdBy;

    @Column(name = "LAST_MODIFIED_ON", nullable = true)
    @org.hibernate.search.annotations.Field(
        index = org.hibernate.search.annotations.Index.UN_TOKENIZED,
        store = org.hibernate.search.annotations.Store.YES
    )
    @org.hibernate.search.annotations.DateBridge(resolution = org.hibernate.search.annotations.Resolution.DAY)
    @Searchable(description = "Last Modified", type = SearchableType.PASTDATE)
    protected Date lastModifiedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LAST_MODIFIED_BY_USER_ID", nullable = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_WIKI_NODE_LAST_MODIFIED_BY")
    protected User lastModifiedBy;

    @Column(name = "WRITE_ACCESS_LEVEL", nullable = false)
    protected int writeAccessLevel = 0;

    @Column(name = "READ_ACCESS_LEVEL", nullable = false)
    @org.hibernate.search.annotations.Field(
        index = org.hibernate.search.annotations.Index.UN_TOKENIZED,
        store = org.hibernate.search.annotations.Store.YES
    )
    @org.hibernate.search.annotations.FieldBridge(impl = PaddedIntegerBridge.class)
    protected int readAccessLevel = 0;

    @Column(name = "WRITE_PROTECTED", nullable = false)
    protected boolean writeProtected = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_NODE_ID", nullable = true)
    //TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @org.hibernate.annotations.ForeignKey(name = "FK_WIKI_NODE_PARENT_NODE_ID")
    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.JOIN)
    protected WikiNode parent;

    @Range(min = 0l, max = 5)
    @Column(name = "RATING", nullable = false)
    private int rating = 0;

    protected WikiNode() {}

    protected WikiNode(String name) {
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAreaNumber() { return areaNumber; }
    public void setAreaNumber(Long areaNumber) { this.areaNumber = areaNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getWikiname() { return wikiname; }
    public void setWikiname(String wikiname) { this.wikiname = wikiname; }

    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public Date getCreatedOn() { return createdOn; }
    public void setCreatedOn(Date createdOn) { this.createdOn = createdOn; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Date getLastModifiedOn() { return lastModifiedOn; }
    public void setLastModifiedOn(Date lastModifiedOn) { this.lastModifiedOn = lastModifiedOn; }

    public User getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(User lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }

    public int getWriteAccessLevel() { return writeAccessLevel; }
    public void setWriteAccessLevel(int writeAccessLevel) { this.writeAccessLevel = writeAccessLevel; }

    public int getReadAccessLevel() { return readAccessLevel; }
    public void setReadAccessLevel(int readAccessLevel) { this.readAccessLevel = readAccessLevel; }

    public boolean isWriteProtected() { return writeProtected; }
    public void setWriteProtected(boolean writeProtected) { this.writeProtected = writeProtected; }

    public WikiNode getParent() { return parent; }
    public void setParent(WikiNode parent) { this.parent = parent; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public WikiNode getArea() {
        if (this.getParent() != null && this.getParent().getParent() == null) return this; // This is an area
        // Else, walk up the tree until we find the area
        WikiNode current = this.getParent();
        while (current != null && current.getParent() != null && current.getParent().getParent() != null) {
            current = current.getParent();
        }
        return current; // Return null if this is the Wiki ROOT
    }

    public int compareTo(Object o) {
        return getName().compareTo( ((WikiNode)o).getName());
    }

    /**
     * Creates copy for display or history archiving.
     * <p>
     * Does <b>NOT</b> copy the node id and object version, so the copy might as well be
     * considered transient and can be persisted right away. If you want to store the
     * copy in the audit log, call setId() manually before on the copy, passing in the
     * identifier value of the original. Note that no collections or entity association
     * (many-to-one, one-to-one) references are copied!
     * </p>
     * @param original The node to make a copy of
     * @param copyLazyProperties Copy (potentially large) properties which are lazily loaded
     */
    public void flatCopy(WikiNode original, boolean copyLazyProperties) {
        this.areaNumber = original.getAreaNumber();
        this.name = original.name;
        this.wikiname = original.wikiname;
        this.lastModifiedOn = original.lastModifiedOn;
        this.writeAccessLevel = original.writeAccessLevel;
        this.readAccessLevel = original.readAccessLevel;
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

    public boolean isOwnedByRegularUser() {
        return getCreatedBy() != null
                && !User.ADMIN_USERNAME.equals(getCreatedBy().getUsername())
                && !User.GUEST_USERNAME.equals(getCreatedBy().getUsername());
    }

    public abstract String getPermURL(String suffix);
    public abstract String getWikiURL();

    public abstract N duplicate(boolean copyLazyProperties);
    
}
