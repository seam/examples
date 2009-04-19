/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.nestedset;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Any subclass has parent or children. This is not really a composite pattern, because
 * even files can have children.
 *
 * @author Christian Bauer
 */
@Entity
@Table(name = "TEST_ITEM")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class TestItem {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "ITEM_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PARENT_ITEM_ID")
    @org.hibernate.annotations.ForeignKey(name = "TEST_FK_ITEM_PARENT_ITEM_ID") // Just a name for the FK constraint
    private TestItem parent;

    @OneToMany(mappedBy = "parent") // Don't cascade PERSIST here, updates of nested set nodes will be in the wrong order
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Set<TestItem> children = new HashSet<TestItem>();

    @Column(name = "ITEM_NAME", unique = true)
    private String name;


    public TestItem() {
        this(null);
    }

    public TestItem(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public TestItem getParent() {
        return parent;
    }

    public Set<TestItem> getChildren() {
        return children;
    }

    public void addChild(TestItem child) {
        if (child.getParent() != null) {
            child.getParent().getChildren().remove(child);
        }
        getChildren().add(child);
        child.parent = this;
    }

    public TestItem removeChild(TestItem child) {
        getChildren().remove(child);
        child.parent = null;
        return child;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String toString() {
        return "Item (" + getId() + "): " + getName();
    }

}
