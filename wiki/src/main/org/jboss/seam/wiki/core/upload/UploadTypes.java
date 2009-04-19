package org.jboss.seam.wiki.core.upload;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.wiki.core.upload.handler.WikiUploadHandler;
import org.jboss.seam.wiki.core.upload.handler.WikiUploadImageHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Metadata map, from file mime type strings to file meta info such as icons, upload handler, etc.
 * <p>
 * This application-scoped map is often searched by key, which are mime type strings.
 *
 * @author Christian Bauer
 */
@Name("uploadTypes")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class UploadTypes {

    public static final String GENERIC_UPLOAD_TYPE = "generic";
    public static final String DEFAULT_UPLOAD_TYPE = "application/octet-stream";

    private Map<String, UploadType> uploadTypes = new HashMap<String, UploadType>() {
        {
            put("image/jpg",                    new UploadType("icon.fileimg.gif", new WikiUploadImageHandler() ));
            put("image/jpeg",                   new UploadType("icon.fileimg.gif", new WikiUploadImageHandler() ));
            put("image/png",                    new UploadType("icon.fileimg.gif", new WikiUploadImageHandler() ));
            put("image/gif",                    new UploadType("icon.fileimg.gif", new WikiUploadHandler() ));
            put("text/plain",                   new UploadType("icon.filetxt.gif", new WikiUploadHandler() ));
            put("application/pdf",              new UploadType("icon.filepdf.gif", new WikiUploadHandler() ));
            put(DEFAULT_UPLOAD_TYPE,            new UploadType("icon.filegeneric.gif", new WikiUploadHandler() ));
            put(GENERIC_UPLOAD_TYPE,            new UploadType("icon.filegeneric.gif", new WikiUploadHandler() ));
        }
    };

    @Unwrap
    public Map<String, UploadType> getUploadTypes() {
        return uploadTypes;
    }

}
