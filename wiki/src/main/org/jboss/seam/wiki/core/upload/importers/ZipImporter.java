package org.jboss.seam.wiki.core.upload.importers;

import net.sf.jmimemagic.Magic;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.core.Validators;
import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import static org.jboss.seam.international.StatusMessage.Severity.INFO;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.upload.UploadType;
import org.jboss.seam.wiki.core.upload.UploadTypes;
import org.jboss.seam.wiki.core.upload.Uploader;
import org.jboss.seam.wiki.core.upload.importers.annotations.UploadImporter;
import org.jboss.seam.wiki.core.upload.importers.metamodel.Importer;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.persistence.EntityManager;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * TODO: Delegate code to util.Unarchiver
 *
 * @author Christian Bauer
 */
@Name("zipImporter")
@UploadImporter(
        handledMimeTypes = {"application/zip", "application/java-archive"},
        handledExtensions = {"zip", "jar"},
        description = "lacewiki.label.ZipImporter"
)
@AutoCreate
public class ZipImporter implements Importer {

    @Logger
    Log log;

    @In
    protected WikiNodeDAO wikiNodeDAO;

    @In
    protected Map<String, UploadType> uploadTypes;

    @In
    protected StatusMessages statusMessages;

    public boolean handleImport(EntityManager em, WikiUpload zipFile) {

        if (zipFile.getData().length == 0) return true;

        Map<String, Object> newObjects = new HashMap<String, Object>();

        ByteArrayInputStream byteStream = null;
        ZipInputStream zipInputStream = null;
        try {
            byteStream = new ByteArrayInputStream(zipFile.getData());
            zipInputStream = new ZipInputStream(new BufferedInputStream(byteStream));

            int                   bufferSize = 1024;
            ZipEntry              ze;
            ByteArrayOutputStream baos;
            byte[]                buffer = new byte[bufferSize];
            byte[]                uncompressedBytes;
            int                   bytesRead;

            while ((ze = zipInputStream.getNextEntry()) != null) {
                log.debug("extracting zip entry: " + ze.getName());

                if (!beforeUncompress(em, zipFile, ze)) continue;

                baos = new ByteArrayOutputStream();
                while ((bytesRead = zipInputStream.read(buffer, 0, bufferSize)) > 0) {
                    baos.write(buffer, 0, bytesRead);
                }
                baos.close();
                uncompressedBytes = baos.toByteArray();

                Object newObject = createNewObject(em, zipFile, ze, uncompressedBytes);
                if (newObject != null) {
                    newObjects.put(ze.getName(), newObject);
                }

                zipInputStream.closeEntry();
            }

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            try {
                if (zipInputStream != null) zipInputStream.close();
                if (byteStream != null) byteStream.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        handleNewObjects(em, zipFile, newObjects);

        return true;
    }

    protected boolean beforeUncompress(EntityManager em, WikiUpload zipFile, ZipEntry zipEntry) {

        if (zipEntry.getName().contains("/")) {
            log.debug("skipping directory: " + zipEntry.getName());
            statusMessages.addFromResourceBundleOrDefault(
                ERROR,
                "lacewiki.msg.ImportSkippingDirectory",
                "Skipping directory '{0}', importing not supported...",
                zipEntry.getName()
            );
            return false; // Not supported
        }

        // This assumes that the wiki name is the zip entry filename without extension - maybe we should
        // support with extension as option, that's unique inside a zip archive so we fail too often
        WikiUpload tmp = new WikiUpload();
        tmp.setFilename(zipEntry.getName());
        return validateNewWikiname(zipFile, WikiUtil.convertToWikiName(tmp.getFilenameWithoutExtension()));
    }

    protected boolean validateNewWikiname(WikiUpload zipFile, String newWikiname) {
        log.debug("validating wiki name of new file: " + newWikiname);

        if (wikiNodeDAO.isUniqueWikiname(zipFile.getAreaNumber(), newWikiname) ) {
            log.debug("new name is unique and valid");
            return true;
        } else {
            log.debug("new name is not unique and invalid");
            statusMessages.addFromResourceBundleOrDefault(
                ERROR,
                "lacewiki.msg.ImportDuplicateName",
                "Skipping file '{0}', name is already used in this area...",
                newWikiname
            );
            return false;
        }
    }

    protected Object createNewObject(EntityManager em, WikiUpload zipFile, ZipEntry zipEntry, byte[] uncompressedBytes) {
        log.debug("creating new file from zip entry: " + zipEntry.getName());

        // First figure out what it is
        String mimeType = null;
        try {
            mimeType = Magic.getMagicMatch(uncompressedBytes).getMimeType();
        } catch (Exception ex) {}
        String contentType = mimeType != null ? mimeType : UploadTypes.DEFAULT_UPLOAD_TYPE;

        // Just a temporary value holder this time
        Uploader uploader = new Uploader();
        uploader.setData(uncompressedBytes);
        uploader.setFilename(zipEntry.getName());
        uploader.setContentType(contentType);

        // Get the right handler for that type and produce a WikiUpload instance
        UploadType uploadType = uploadTypes.get(contentType);
        if (uploadType == null) uploadType = uploadTypes.get(UploadTypes.GENERIC_UPLOAD_TYPE);
        WikiUpload wikiUpload = uploadType.getUploadHandler().handleUpload(uploader);

        // Now set the other properties so we can persist it directly
        wikiUpload.setName(wikiUpload.getFilenameWithoutExtension());
        wikiUpload.setWikiname(WikiUtil.convertToWikiName(wikiUpload.getName()));
        wikiUpload.setAreaNumber(zipFile.getAreaNumber());
        wikiUpload.setCreatedBy(zipFile.getCreatedBy());
        wikiUpload.setLastModifiedBy(wikiUpload.getCreatedBy());
        wikiUpload.setCreatedOn(new Date(zipEntry.getTime()));
        wikiUpload.setLastModifiedOn(wikiUpload.getCreatedOn());
        wikiUpload.setReadAccessLevel(zipFile.getReadAccessLevel());
        wikiUpload.setWriteAccessLevel(zipFile.getWriteAccessLevel());

        log.debug("created new file from zip entry: " + wikiUpload);

        return wikiUpload;
    }

    public void handleNewObjects(EntityManager em, WikiUpload zipFile, Map<String, Object> newObjects) {
        persistWikiUploadsSorted(
            em,
            zipFile,
            newObjects,
            new Comparator() {
                public int compare(Object o, Object o1) {
                    if ( !(o instanceof WikiNode) &&  !(o1 instanceof WikiNode) ) return 0;
                    return ((WikiNode)o).getWikiname().compareTo( ((WikiNode)o1).getWikiname() );
                }
            }
        );
    }

    private void persistWikiUploadsSorted(EntityManager em, WikiUpload zipFile, Map<String, Object> newObjects, Comparator comparator) {

        List<WikiNode> newNodes = new ArrayList<WikiNode>();
        for (Object newObject : newObjects.values()) {
            if (newObject instanceof WikiNode) {
                newNodes.add((WikiNode)newObject);
            }
        }
        Collections.sort(newNodes, comparator);

        for (WikiNode newNode : newNodes) {
            log.debug("validating new node");

            ClassValidator validator = Validators.instance().getValidator(newNode.getClass());
            InvalidValue[] invalidValues = validator.getInvalidValues(newNode);
            if (invalidValues != null && invalidValues.length > 0) {
                log.debug("new node is invalid: " + newNode);
                for (InvalidValue invalidValue : invalidValues) {
                    statusMessages.addFromResourceBundleOrDefault(
                        ERROR,
                        "lacewiki.msg.ImportInvalidNode",
                        "Skipping entry '{0}', invalid: {1}",
                        newNode.getName(),
                        invalidValue.getMessage()

                    );
                }
                continue;
            }

            log.debug("persisting newly imported node: " + newNode);
            newNode.setParent(zipFile.getParent());
            em.persist(newNode);
            statusMessages.addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.ImportOk",
                "Created file '{0}' in current directory.",
                newNode.getName()
            );
        }
    }


}
