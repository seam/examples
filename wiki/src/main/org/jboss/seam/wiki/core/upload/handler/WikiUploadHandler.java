package org.jboss.seam.wiki.core.upload.handler;

import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.upload.editor.UploadEditor;
import org.jboss.seam.wiki.core.upload.editor.WikiUploadEditor;

public class WikiUploadHandler<WU extends WikiUpload> extends UploadHandler<WU> {

    public UploadEditor<WikiUpload> createEditor(WikiUpload instance) {
        try {
            UploadEditor<WikiUpload> editor = WikiUploadEditor.class.newInstance();
            editor.init(instance);
            return editor;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
