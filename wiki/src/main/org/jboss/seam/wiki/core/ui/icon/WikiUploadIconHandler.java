/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui.icon;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.upload.UploadType;
import org.jboss.seam.wiki.core.upload.UploadTypes;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Christian Bauer
 */
@Name("wikiUploadIconHandler")
public class WikiUploadIconHandler extends IconHandler<WikiUpload> implements Serializable {

    @In
    Map<String, UploadType> uploadTypes;

    public String getIconName(WikiUpload upload) {
        if (uploadTypes.containsKey(upload.getContentType()))
            return uploadTypes.get(upload.getContentType()).getDisplayIcon();
        return uploadTypes.get(UploadTypes.GENERIC_UPLOAD_TYPE).getDisplayIcon();
    }
}