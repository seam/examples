/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.nestedset;

import org.jboss.seam.wiki.core.nestedset.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "TEST_DIRECTORY")
@org.hibernate.annotations.ForeignKey(name = "TEST_FK_DIRECTORY_ITEM_ID")
@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
public class TestDirectory extends TestItem implements NestedSetNode<TestDirectory> {

    @Embedded
    private NestedSetNodeInfo<TestDirectory> nodeInfo;

    @Column(name = "JUST_A_DATE")
    private Date justADate = new Date();

    public TestDirectory() {
        super();
        nodeInfo = new NestedSetNodeInfo<TestDirectory>(this);
    }

    public TestDirectory(String name) {
        super(name);
        nodeInfo = new NestedSetNodeInfo<TestDirectory>(this);
    }

    public NestedSetNodeInfo<TestDirectory> getNodeInfo() {
        return nodeInfo;
    }

    public NestedSetNodeInfo<TestDirectory> getParentNodeInfo() {
        if (getParent() != null && TestDirectory.class.isAssignableFrom(getParent().getClass()))
            return ((TestDirectory)getParent()).getNodeInfo();
        return null;
    }

    public String[] getPropertiesForGroupingInQueries() {
        return new String[] { "parent", "name", "justADate"};
    }

    public String[] getLazyPropertiesForGroupingInQueries() {
        return new String[0];
    }

    public Date getJustADate() {
        return justADate;
    }

    public void setJustADate(Date justADate) {
        this.justADate = justADate;
    }
}
