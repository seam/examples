package org.jboss.seam.wiki.core.upload.editor;

import org.jboss.seam.wiki.core.model.WikiUpload;

public abstract class UploadEditor<WU extends WikiUpload> {

    private WU instance;

    public void init(WU instance) {
        this.instance = instance;
    }

    public abstract String getIncludeName();

    public WU getInstance() {
        return instance;
    }

    /**
     * Called after superclass did its preparation right before the actual persist()
     * @return boolean continue processing
     */
    public boolean beforePersist() { return true; }

    /**
     * Called after superclass did its preparation right before the actual update()
     * @return boolean continue processing
     */
    public boolean beforeUpdate() { return true; }

}
