package org.jboss.seam.wiki.core.upload;

import org.jboss.seam.wiki.core.upload.handler.UploadHandler;

public class UploadType {

    private String displayIcon;
    private UploadHandler uploadHandler;

    public String getDisplayIcon() {
        return displayIcon;
    }

    public UploadHandler getUploadHandler() {
        return uploadHandler;
    }

    public UploadType(String displayIcon, UploadHandler uploadHandler) {
        this.displayIcon = displayIcon;
        this.uploadHandler = uploadHandler;
    }

}
