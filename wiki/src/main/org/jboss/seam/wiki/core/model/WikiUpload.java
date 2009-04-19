/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import javax.persistence.*;

@Entity
@Table(name = "WIKI_UPLOAD")
@org.hibernate.annotations.ForeignKey(name = "FK_WIKI_UPLOAD_NODE_ID")
//TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
public class WikiUpload<U extends WikiUpload> extends WikiFile<U> {

    @Column(name = "FILENAME", length = 255, nullable = false)
    private String filename;

    @Column(name = "FILESIZE", nullable = false)
    private int filesize;

    // SchemaExport needs length.. MySQL has "tinyblob", "mediumblob" and other such nonsense types
    @org.hibernate.annotations.Type(type = "org.jboss.seam.wiki.util.BinaryBlobType")
    @Column(name = "FILEDATA", nullable = false, length = 10000000)
    @Basic(fetch = FetchType.LAZY) // Lazy loaded through bytecode instrumentation
    private byte[] data;

    @Column(name = "CONTENT_TYPE", length = 255)
    private String contentType;

    public WikiUpload() { super("New File"); }

    public WikiUpload(String name) {
        super(name);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getHistoricalEntityName() {
        return null;
    }

    public String getFeedDescription() {
        return getFilename() + ", " + getContentType();
    }

    public String getFilenameWithoutExtension() {
        if (getFilename().contains(".")) {
            return getFilename().substring(0, getFilename().length()-getExtension().length()-1);
        } else {
            return getFilename();
        }
    }
    public String getExtension() {
        if (getFilename().contains(".")) {
            return getFilename().substring( getFilename().lastIndexOf(".")+1, getFilename().length());
        } else {
            return null;
        }
    }

    public void flatCopy(WikiUpload original, boolean copyLazyProperties) {
        super.flatCopy(original, copyLazyProperties);
        this.filename = original.getFilename();
        this.filesize = original.getFilesize();
        this.contentType = original.getContentType();
        if (copyLazyProperties) {
            this.data = original.getData();
        }
    }

    public U duplicate(boolean copyLazyProperties) {
        U dupe = (U)new WikiUpload();
        dupe.flatCopy(this, copyLazyProperties);
        return dupe;
    }

    public String getPermURL(String suffix) {
        return "service/File/" + getId();
    }

    public String getWikiURL() {
        return "service/File/" + getId();
    }

    public String toString() {
        return "WikiUpload (" + getId() + "): " + getName() + ", " + getFilename();
    }

    public boolean isAttachedToDocuments() {
        return true;
    }

}

