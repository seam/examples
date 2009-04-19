/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.nestedset;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;

@Entity
@Table(name = "TEST_FILE")
@org.hibernate.annotations.ForeignKey(name = "TEST_FK_FILE_ITEM_ID")
@org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
public class TestFile extends TestItem {

    public TestFile() {}

    public TestFile(String name) {
        super(name);
    }

    @Column(name = "FILENAME")
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
