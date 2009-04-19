package org.jboss.seam.wiki.util;

import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author Christian Bauer
 */
public class Unarchiver {

    Log log = Logging.getLog(Unarchiver.class);

    protected Handler handler;

    public Unarchiver(Handler handler) {
        this.handler = handler;
    }

    public void extract(WikiUpload zipFile) {
        if (zipFile == null) {
            throw new IllegalArgumentException("Archive must not be null");
        }

        if (zipFile.getData().length == 0) return;

        Map<String, Object> newObjects = new HashMap<String, Object>();

        ByteArrayInputStream byteStream = null;
        ZipInputStream zipInputStream = null;
        try {
            byteStream = new ByteArrayInputStream(zipFile.getData());
            zipInputStream = new ZipInputStream(new BufferedInputStream(byteStream));

            int bufferSize = 1024;
            ZipEntry ze;
            ByteArrayOutputStream baos;
            byte[] buffer = new byte[bufferSize];
            byte[] uncompressedBytes;
            int bytesRead;

            while ((ze = zipInputStream.getNextEntry()) != null) {
                log.trace("extracting zip entry: " + ze.getName());

                if (!handler.beforeUncompress(zipFile, ze)) continue;

                baos = new ByteArrayOutputStream();
                while ((bytesRead = zipInputStream.read(buffer, 0, bufferSize)) > 0) {
                    baos.write(buffer, 0, bytesRead);
                }
                baos.close();
                uncompressedBytes = baos.toByteArray();

                Object newObject = handler.createNewObject(zipFile, ze, uncompressedBytes);
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

        handler.handleNewObjects(zipFile, newObjects);
    }

    public interface Handler<T> {
        public boolean beforeUncompress(WikiUpload zipFile, ZipEntry zipEntry);
        public T createNewObject(WikiUpload zipFile, ZipEntry zipEntry, byte[] uncompressedBytes);
        public abstract void handleNewObjects(WikiUpload zipFile, Map<String, T> newObjects);
    }
}
