/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.action.TagQuery;
import org.jboss.seam.wiki.core.action.UploadHome;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.List;

public class Tagging extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/UploadData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void tagDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);

                assert docHome.getInstance().getId().equals(6l); // Init!

                assert docHome.getInstance().getTags().size() == 1;
                assert docHome.getInstance().getTags().contains("Tag One");
                assert docHome.getTagEditor().getTagsAsList().contains("Tag One");
                docHome.getTagEditor().setNewTag("New Tag");
                docHome.getTagEditor().addNewTag();
                assert invokeMethod("#{documentHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getTags().size() == 2;
                assert docHome.getInstance().getTagsAsList().get(0).equals("New Tag");
                assert docHome.getInstance().getTagsAsList().get(1).equals("Tag One");

                TagQuery tagQuery = (TagQuery)getInstance(TagQuery.class);
                tagQuery.setTag("New Tag");
                List<WikiFile> taggedFiles = tagQuery.getTaggedFiles();
                assert taggedFiles.size() == 1;
                assert taggedFiles.get(0).getId().equals(6l);
            }
        }.run();
    }

    @Test
    public void tagUpload() throws Exception {

        final String conversationId = new NonFacesRequest("/uploadEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("uploadId", "30");
                setParameter("parentDirectoryId", "2");
            }
        }.run();

        new FacesRequest("/uploadEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                UploadHome uploadHome = (UploadHome)getInstance(UploadHome.class);
                assert uploadHome.getInstance().getId().equals(30l); // Init!

                assert uploadHome.getInstance().getTags().size() == 0;
                uploadHome.getTagEditor().setNewTag("New Tag");
                uploadHome.getTagEditor().addNewTag();
                assert invokeMethod("#{uploadHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                UploadHome uploadHome = (UploadHome)getInstance(UploadHome.class);
                assert uploadHome.getInstance().getTags().size() == 1;
                assert uploadHome.getInstance().getTagsAsList().get(0).equals("New Tag");

                TagQuery tagQuery = (TagQuery)getInstance(TagQuery.class);
                tagQuery.setTag("New Tag");
                List<WikiFile> taggedFiles = tagQuery.getTaggedFiles();
                assert taggedFiles.size() == 1;
                assert taggedFiles.get(0).getId().equals(30l);
            }
        }.run();
    }

}