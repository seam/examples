package org.jboss.seam.wiki.core.upload;

import net.sf.jmimemagic.Magic;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.upload.handler.UploadHandler;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import static org.jboss.seam.international.StatusMessage.Severity.WARN;

import java.util.Map;
import java.io.Serializable;

/**
 * A handy conversation-scoped component we can use to bind a typical upload form to.
 * <p>
 * Call the <tt>uploadNewInstance()</tt> action and then access <tt>getUpload()</tt> in the same
 * conversation. Uses upload handlers to instantiate and fill the data into the right
 * <tt>WikiUpload</tt> subclass.
 * <p>
 * Call the <tt>uploadUpdateInstance(id)</tt> action to retrieve and fill the data into
 * an existing <tt>WikiUpload</tt> subclass instance.
 *
 * @author Christian Bauer
 */
@Name("uploader")
@Scope(ScopeType.CONVERSATION)
public class Uploader implements Serializable {

    @Logger
    Log log;

    @In
    private StatusMessages statusMessages;

    @In
    Map<String, UploadType> uploadTypes;

    UploadHandler handler;

    WikiUpload upload;

    private String filename;
    private String contentType;
    private byte[] data;
    Long parentDirectoryId;

    public UploadHandler getUploadHandler() {
        return handler;
    }

    public WikiUpload getUpload() {
        return upload;
    }

    public Long getParentDirectoryId() {
        return parentDirectoryId;
    }

    public void setParentDirectoryId(Long parentDirectoryId) {
        this.parentDirectoryId = parentDirectoryId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    /**
     * Marshall form data into a new <tt>WikiUpload</tt> instance, pick
     * the right handler automatically based on the data (or browser, if magic fails) content type.
     *
     * @return String outcome of action, null or if successful, the simple classname of the created entity.
     */
    public String uploadNewInstance() {
        log.debug("uploading new instance");
        if (!validateData()) return null;
        resolveContentType();
        UploadType uploadType = uploadTypes.get(contentType);
        if (uploadType == null) {
            uploadType = uploadTypes.get(UploadTypes.GENERIC_UPLOAD_TYPE);
        }
        upload = uploadType.getUploadHandler().handleUpload(this);
        handler = uploadType.getUploadHandler();
        log.debug("uploaded: " + upload);
        return upload.getClass().getSimpleName();
    }

    /**
     * Marshall form data into existing <tt>WikiUpload</tt> instance, pick
     * the right handler automatically based on (possibly new) content type.
     *
     * @param instance The instance to be updated
     * @return String outcome of action, null or if successful, the simple classname of the updated entity.
     */
    public String uploadUpdateInstance(WikiUpload instance) {
        return uploadUpdateInstance(instance, false);
    }

    /**
     * Marshall form data into existing <tt>WikiUpload</tt> instance.
     * <p>
     * Optionally you can enforce that the same upload handler class should be used
     * as has been used for the existing <tt>WikiUpload</tt>. For example, if the
     * existing entity is a <tt>WikiUploadImage</tt>, its upload handler would be
     * <tt>WikiUploadImageHandler</tt>, based on the content type we stored along with
     * the entity. If the newly uploaded data/content type does not produce the same
     * handler class, a JSF message will be queued, <tt>getUpdate()</tt> will return null,
     * and no data will be marshalled.
     *
     * @param instance The instance to be updated
     * @param forceSameHandler Force the same handler
     * @return String outcome of action, null or if successful, the simple classname of the updated entity.
     */
    public String uploadUpdateInstance(WikiUpload instance, boolean forceSameHandler) {
        log.debug("uploading and updating existing instance: " + instance);
        this.upload = instance;
        if (!validateData()) return null;
        resolveContentType();

        UploadType newUploadType = uploadTypes.get(contentType);
        UploadHandler newupUploadHandler =
                newUploadType != null
                ? newUploadType.getUploadHandler()
                : uploadTypes.get(UploadTypes.GENERIC_UPLOAD_TYPE).getUploadHandler();

        if (forceSameHandler) {
            log.debug("using same upload handler as for the original, based on content type: " + instance.getContentType());
            UploadHandler previousUploadHandler;
            UploadType previousUploadType = uploadTypes.get(instance.getContentType());
            previousUploadHandler =
                    previousUploadType != null
                    ? previousUploadType.getUploadHandler()
                    : uploadTypes.get(UploadTypes.GENERIC_UPLOAD_TYPE).getUploadHandler();
            if (!previousUploadHandler.getClass().equals(newupUploadHandler.getClass())) {
                statusMessages.addFromResourceBundleOrDefault(
                    ERROR,
                    "lacewiki.msg.upload.HandlersDontMatch",
                    "Wrong file type uploaded, please try again with a different file."
                );
                upload = null;
                return null;
            }
        }

        log.debug("using upload handler to marshall data: " + newupUploadHandler.getClass());
        upload = newupUploadHandler.handleUpload(this, upload);
        handler = newupUploadHandler;
        log.debug("uploaded: " + upload);
        return upload.getClass().getSimpleName();
    }

    public boolean hasData() {
        return data != null && data.length > 0;
    }

    public boolean validateData() {
        if (data == null || data.length == 0) {
            statusMessages.addFromResourceBundleOrDefault(
                WARN,
                "lacewiki.msg.upload.NoData",
                "Please select a file to upload"
            );
            return false;
        }
        return true;
    }

    public void reset() {
        filename = null;
        contentType = null;
        data = null;
    }

    // Use mime magic to find the "real" content type - but if there is an exception
    // (which is expected because JMimeMagic is crap) - use the browser-supplied type.
    protected void resolveContentType() {
        String mimeType = null;
        try {
            mimeType = Magic.getMagicMatch(data).getMimeType();
        } catch (Exception ex) {}
        contentType = mimeType != null ? mimeType : contentType;
    }

}
