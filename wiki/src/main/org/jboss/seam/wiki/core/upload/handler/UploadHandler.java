package org.jboss.seam.wiki.core.upload.handler;

import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.upload.editor.UploadEditor;
import org.jboss.seam.wiki.core.upload.Uploader;

public abstract class UploadHandler<WU extends WikiUpload> {

    public abstract UploadEditor<WU> createEditor(WU instance);

    public WU handleUpload(Uploader uploader) {
        WU newWikiUpload = createEntityInstance();
        marshallData(uploader, newWikiUpload);
        return newWikiUpload;
    }

    public WU handleUpload(Uploader uploader, WU updateInstance) {
        marshallData(uploader, updateInstance);
        return updateInstance;
    }

    protected WU createEntityInstance() {
        try {
            return (WU) WikiUpload.class.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void marshallData(Uploader uploader, WU entityInstance) {
        entityInstance.setFilename(uploader.getFilename());
        entityInstance.setContentType(uploader.getContentType());
        entityInstance.setData(uploader.getData());
        entityInstance.setFilesize(uploader.getData().length);
    }


}
