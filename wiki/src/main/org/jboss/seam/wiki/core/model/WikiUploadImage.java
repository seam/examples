package org.jboss.seam.wiki.core.model;

import javax.persistence.*;

@Entity
@Table(name = "WIKI_UPLOAD_IMAGE")
@org.hibernate.annotations.ForeignKey(name = "FK_WIKI_UPLOAD_IMAGE_NODE_ID")
//TODO: @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
public class WikiUploadImage extends WikiUpload<WikiUploadImage> {

    public static enum Thumbnail {
        SMALL('S'), MEDIUM('M'), LARGE('L'), FULL('F'), ATTACHMENT('A');
        private char flag;
        Thumbnail(char flag) {
            this.flag = flag;
        }
        public char getFlag() {
            return flag;
        }
    }

    @Column(name = "SIZE_X")
    private int sizeX;

    @Column(name = "SIZE_Y")
    private int sizeY;

    @Column(name = "THUMBNAIL")
    private char thumbnail = Thumbnail.MEDIUM.getFlag(); // Medium size thumbnail by default, not attached

    // SchemaExport needs length.. MySQL has "tinyblob", "mediumblob" and other such nonsense types
    @org.hibernate.annotations.Type(type = "org.jboss.seam.wiki.util.BinaryBlobType")
    @Column(name = "THUMBNAIL_DATA", length = 1000000, nullable = true)
    @Basic(fetch = FetchType.LAZY) // Lazy loaded through bytecode instrumentation
    private byte[] thumbnailData;

    public WikiUploadImage() {}

    public WikiUploadImage(int sizeX, int sizeY, char thumbnail) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.thumbnail = thumbnail;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public char getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(char thumbnail) {
        this.thumbnail = thumbnail;
    }

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public boolean isAttachedToDocuments() {
        return getThumbnail() == Thumbnail.ATTACHMENT.getFlag();
    }

    public void flatCopy(WikiUploadImage original, boolean copyLazyProperties) {
        super.flatCopy(original, copyLazyProperties);
        this.sizeX = original.sizeX;
        this.sizeY = original.sizeY;
        this.thumbnail = original.thumbnail;
        if (copyLazyProperties) {
            this.thumbnailData = original.thumbnailData;
        }
    }

    public WikiUploadImage duplicate(boolean copyLazyProperties) {
        WikiUploadImage dupe = new WikiUploadImage();
        dupe.flatCopy(this, copyLazyProperties);
        return dupe;
    }

    public String toString() {
        return "WikiUploadImage (" + getId() + "): " + getName()
                + ", " + getFilename() + ", Size: " + getSizeX() + "x" + getSizeY();
    }

}
