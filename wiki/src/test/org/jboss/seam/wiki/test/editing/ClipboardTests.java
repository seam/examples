/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.Clipboard;
import org.jboss.seam.wiki.core.action.DirectoryBrowser;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.List;

public class ClipboardTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/UploadData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test(groups="jdk6-expected-failures")
    public void copyPasteDocument() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(9l);

                browser.getSelectedNodes().put(doc, true);

                browser.copy();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 1;
                assert clipboard.getItemsAsList().get(0).equals(9l);
                assert !clipboard.isCut(9l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;
                
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                assert browser.getChildNodes().size() == 2;

                WikiDocument doc =
                        browser.getWikiNodeDAO().findWikiDocumentInArea(browser.getInstance().getAreaNumber(), "Four");

                assert doc.getAreaNumber().equals(browser.getInstance().getAreaNumber());

                WikiDocument docOriginal =
                        browser.getWikiNodeDAO().findWikiDocumentInArea(2l, "Four");

                assert docOriginal.getParent().getId().equals(2l);
            }
        }.run();

    }

    @Test(groups="jdk6-expected-failures")
    public void copyPasteUploadImage() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiUpload upload = browser.getWikiNodeDAO().findWikiUpload(30l);

                browser.getSelectedNodes().put(upload, true);

                browser.copy();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 1;
                assert clipboard.getItemsAsList().get(0).equals(30l);
                assert !clipboard.isCut(30l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                assert browser.getChildNodes().size() == 2;

                List<WikiUpload> uploads = browser.getWikiNodeDAO().findWikiUploads(browser.getInstance(), WikiNode.SortableProperty.createdOn, true);

                assert uploads.size() == 1;
                assert uploads.get(0).getName().equals("Test Image");
                assert uploads.get(0).getAreaNumber().equals(browser.getInstance().getAreaNumber());

                WikiDirectory originalDir = browser.getWikiNodeDAO().findWikiDirectory(2l);
                List<WikiUpload> originalUploads = browser.getWikiNodeDAO().findWikiUploads(originalDir, WikiNode.SortableProperty.createdOn, true);
                assert originalUploads.size() == 2;
            }
        }.run();

    }

    @Test(groups="jdk6-expected-failures")
    public void copyPasteMultiple() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(9l);
                WikiUpload upload = browser.getWikiNodeDAO().findWikiUpload(30l);

                browser.getSelectedNodes().put(doc, true);
                browser.getSelectedNodes().put(upload, true);

                browser.copy();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 2;
                assert !clipboard.isCut(9l);
                assert !clipboard.isCut(30l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                assert browser.getChildNodes().size() == 3;

                WikiDocument doc =
                        browser.getWikiNodeDAO().findWikiDocumentInArea(browser.getInstance().getAreaNumber(), "Four");

                assert doc.getAreaNumber().equals(browser.getInstance().getAreaNumber());

                List<WikiUpload> uploads = browser.getWikiNodeDAO().findWikiUploads(browser.getInstance(), WikiNode.SortableProperty.createdOn, true);

                assert uploads.size() == 1;
                assert uploads.get(0).getName().equals("Test Image");
                assert uploads.get(0).getAreaNumber().equals(browser.getInstance().getAreaNumber());

                WikiDirectory originalDir = browser.getWikiNodeDAO().findWikiDirectory(2l);
                List<WikiUpload> originalUploads = browser.getWikiNodeDAO().findWikiUploads(originalDir, WikiNode.SortableProperty.createdOn, true);
                assert originalUploads.size() == 2;
            }
        }.run();

    }

    @Test
    public void cutPasteDocument() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(9l);

                browser.getSelectedNodes().put(doc, true);

                browser.cut();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 1;
                assert clipboard.getItemsAsList().get(0).equals(9l);
                assert clipboard.isCut(9l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                assert browser.getChildNodes().size() == 2;

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocumentInArea(browser.getInstance().getAreaNumber(), "Four");
                assert doc.getAreaNumber().equals(browser.getInstance().getAreaNumber());

                WikiDocument docOriginal = browser.getWikiNodeDAO().findWikiDocumentInArea(2l, "Four");
                assert docOriginal == null;
            }
        }.run();

    }

    @Test
    public void cutPasteUploadImage() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiUpload upload = browser.getWikiNodeDAO().findWikiUpload(30l);

                browser.getSelectedNodes().put(upload, true);

                browser.cut();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 1;
                assert clipboard.getItemsAsList().get(0).equals(30l);
                assert clipboard.isCut(30l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                assert browser.getChildNodes().size() == 2;

                List<WikiUpload> uploads = browser.getWikiNodeDAO().findWikiUploads(browser.getInstance(), WikiNode.SortableProperty.createdOn, true);

                assert uploads.size() == 1;
                assert uploads.get(0).getName().equals("Test Image");
                assert uploads.get(0).getAreaNumber().equals(browser.getInstance().getAreaNumber());

                WikiDirectory originalDir = browser.getWikiNodeDAO().findWikiDirectory(2l);
                List<WikiUpload> originalUploads = browser.getWikiNodeDAO().findWikiUploads(originalDir, WikiNode.SortableProperty.createdOn, true);
                assert originalUploads.size() == 1;
            }
        }.run();

    }

    @Test
    public void cutPasteMultiple() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(9l);
                WikiUpload upload = browser.getWikiNodeDAO().findWikiUpload(30l);

                browser.getSelectedNodes().put(doc, true);
                browser.getSelectedNodes().put(upload, true);

                browser.cut();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 2;

                assert clipboard.isCut(9l);
                assert clipboard.isCut(30l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "4");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(4l); // Init!

                assert browser.getChildNodes().size() == 3;

                WikiDocument doc =
                        browser.getWikiNodeDAO().findWikiDocumentInArea(browser.getInstance().getAreaNumber(), "Four");

                assert doc.getAreaNumber().equals(browser.getInstance().getAreaNumber());

                List<WikiUpload> uploads = browser.getWikiNodeDAO().findWikiUploads(browser.getInstance(), WikiNode.SortableProperty.createdOn, true);

                assert uploads.size() == 1;
                assert uploads.get(0).getName().equals("Test Image");
                assert uploads.get(0).getAreaNumber().equals(browser.getInstance().getAreaNumber());

                WikiDocument docOriginal = browser.getWikiNodeDAO().findWikiDocumentInArea(2l, "Four");
                assert docOriginal == null;

                WikiDirectory originalDir = browser.getWikiNodeDAO().findWikiDirectory(2l);
                List<WikiUpload> originalUploads = browser.getWikiNodeDAO().findWikiUploads(originalDir, WikiNode.SortableProperty.createdOn, true);
                assert originalUploads.size() == 1;
            }
        }.run();

    }

    @Test
    public void cutPasteSameArea() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(9l);

                browser.getSelectedNodes().put(doc, true);

                browser.cut();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 1;
                assert clipboard.getItemsAsList().get(0).equals(9l);
                assert clipboard.isCut(9l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                assert browser.getChildNodes().size() == 3;

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocumentInArea(browser.getInstance().getAreaNumber(), "Four");
                assert doc.getAreaNumber().equals(browser.getInstance().getAreaNumber());
                assert doc.getId().equals(9l);
            }
        }.run();

    }

    @Test(groups="jdk6-expected-failures")
    public void copyPasteSameArea() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(9l);

                browser.getSelectedNodes().put(doc, true);

                browser.copy();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 1;
                assert clipboard.getItemsAsList().get(0).equals(9l);
                assert !clipboard.isCut(9l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                assert browser.getChildNodes().size() == 4;
            }
        }.run();

    }

    @Test
    public void cutPasteDocumentWithComments() throws Exception {

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "3");
            }

            protected void invokeApplication() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(3l); // Init!

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(6l);

                browser.getSelectedNodes().put(doc, true);

                browser.cut();
            }

        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "3");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);

                assert clipboard.getItems().size() == 1;
                assert clipboard.getItemsAsList().get(0).equals(6l);
                assert clipboard.isCut(6l);
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                browser.paste();

                browser.getEntityManager().flush(); // TODO: ?! I think the test phase listener is wrong here not doing that...
            }
        }.run();

        new FacesRequest("/dirDisplay_d.xhtml") {

            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }

            protected void renderResponse() throws Exception {
                Clipboard clipboard = (Clipboard)getInstance(Clipboard.class);
                assert clipboard.getItems().size() == 0;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                assert browser.getInstance().getId().equals(2l); // Init!

                assert browser.getChildNodes().size() == 4;

                WikiDocument docOriginal = browser.getWikiNodeDAO().findWikiDocumentInArea(3l, "One");
                assert docOriginal == null;

                WikiDirectory dirOriginal = browser.getWikiNodeDAO().findWikiDirectory(3l);
                assert dirOriginal.getDefaultFile() == null;

                WikiDocument doc = browser.getWikiNodeDAO().findWikiDocument(6l);
                assert doc.getAreaNumber().equals(2l);
                assert doc.getParent().getId().equals(2l);

                List<WikiComment> comments = browser.getWikiNodeDAO().findWikiCommentsFlat(doc, false);
                for (WikiComment comment : comments) {
                    assert comment.getAreaNumber().equals(2l);
                }
                
            }
        }.run();

    }

}