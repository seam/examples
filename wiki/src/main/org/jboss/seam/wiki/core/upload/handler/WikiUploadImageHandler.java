package org.jboss.seam.wiki.core.upload.handler;

import org.jboss.seam.wiki.core.model.WikiUploadImage;
import org.jboss.seam.wiki.core.upload.Uploader;
import org.jboss.seam.wiki.core.upload.editor.UploadEditor;
import org.jboss.seam.wiki.core.upload.editor.WikiUploadImageEditor;

import javax.swing.*;

public class WikiUploadImageHandler extends UploadHandler<WikiUploadImage>  {

    public UploadEditor<WikiUploadImage> createEditor(WikiUploadImage instance) {
        try {
            UploadEditor<WikiUploadImage> editor = WikiUploadImageEditor.class.newInstance();
            editor.init(instance);
            return editor;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected WikiUploadImage createEntityInstance() {
        try {
            return WikiUploadImage.class.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public WikiUploadImage handleUpload(Uploader uploader) {
        WikiUploadImage image = super.handleUpload(uploader);
        marshallData(uploader, image);
        return image;
    }

    public WikiUploadImage handleUpload(Uploader uploader, WikiUploadImage updateInstance) {
        WikiUploadImage image = super.handleUpload(uploader, updateInstance);
        marshallData(uploader, image);
        return image;
    }

    private void marshallData(Uploader uploader, WikiUploadImage image) {

        // Calculate image size
        ImageIcon icon = new ImageIcon(image.getData());
        int imageSizeX = icon.getImage().getWidth(null);
        int imageSizeY = icon.getImage().getHeight(null);
        image.setSizeX(imageSizeX);
        image.setSizeY(imageSizeY);
        
    }

}
