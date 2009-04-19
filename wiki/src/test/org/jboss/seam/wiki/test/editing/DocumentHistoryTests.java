/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DocumentHistoryTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void checkRevisions() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", "6");
            }

            protected void renderResponse() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                WikiDocument doc = (WikiDocument)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(6l);

                assert doc.getRevision() == 3;

                assert docHome.isHistoricalFilesPresent();
                assert docHome.getHistoricalFiles().size() == 3;

                // Sorted by revision descending
                assert docHome.getHistoricalFiles().get(0).getRevision() == 2;
                assert docHome.getHistoricalFiles().get(0).getId().equals(6l);
                assert docHome.getHistoricalFiles().get(0).getLastModifiedByUsername().equals("admin");
                assert docHome.getHistoricalFiles().get(0).getLastModifiedOn() != null;
                assert !docHome.getHistoricalFiles().get(0).getLastModifiedOn().equals(doc.getLastModifiedOn());

                assert docHome.getHistoricalFiles().get(1).getRevision() == 1;
                assert docHome.getHistoricalFiles().get(0).getId().equals(6l);
                assert docHome.getHistoricalFiles().get(1).getLastModifiedByUsername().equals("guest");
                assert docHome.getHistoricalFiles().get(1).getLastModifiedOn() != null;
                assert !docHome.getHistoricalFiles().get(1).getLastModifiedOn().equals(doc.getLastModifiedOn());

                assert docHome.getHistoricalFiles().get(2).getRevision() == 0;
                assert docHome.getHistoricalFiles().get(0).getId().equals(6l);
                assert docHome.getHistoricalFiles().get(2).getLastModifiedByUsername().equals("admin");
                assert docHome.getHistoricalFiles().get(2).getLastModifiedOn() == null;
            }

        }.run();
    }

    @Test(groups="jdk6-expected-failures")
    public void createDocumentCheckRevision() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                docHome.getInstance().setName("Test Name");
                docHome.getTextEditor().setValue("Test Content");

                assert invokeMethod("#{documentHome.persist}").equals("persisted");

            }

            protected void renderResponse() throws Exception {
                WikiFile newFile = (WikiFile)getValue("#{documentHome.instance}");

                // Shouldn't have any historical data
                WikiNodeDAO wikiNodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                assert wikiNodeDAO.findHistoricalFiles(newFile).size() == 0;
                assert wikiNodeDAO.findNumberOfHistoricalFiles(newFile).equals(0l);
            }

        }.run();
    }

    @Test(groups="jdk6-expected-failures")
    public void updateDocumentCheckRevisions() throws Exception {

        loginAdmin();

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "6");
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            String oldContent;

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                docHome.syncInstanceToEditor(docHome.getParentNode().getId(), docHome.getInstance());
                docHome.getTextEditor().setValue("New text");
                docHome.syncEditorToInstance(docHome.getParentNode().getId(), docHome.getInstance());
                docHome.setMinorRevision(false);

                assert invokeMethod("#{documentHome.update}").equals("updated");
            }

        }.run();

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", "6");
            }

            protected void renderResponse() throws Exception {
                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                assert docHome.getInstance().getId().equals(6l); // Init!

                assert docHome.isHistoricalFilesPresent();
                assert docHome.getHistoricalFiles().size() == 4;

                // Sorted by revision descending
                assert docHome.getHistoricalFiles().get(0).getRevision() == 3;
                assert docHome.getHistoricalFiles().get(0).getId().equals(6l);
                assert docHome.getHistoricalFiles().get(0).getLastModifiedByUsername().equals("admin");
                assert docHome.getHistoricalFiles().get(0).getLastModifiedOn() != null;

                assert docHome.getHistoricalFiles().get(1).getRevision() == 2;
                assert docHome.getHistoricalFiles().get(0).getId().equals(6l);
                assert docHome.getHistoricalFiles().get(1).getLastModifiedByUsername().equals("admin");
                assert docHome.getHistoricalFiles().get(1).getLastModifiedOn() != null;

                assert docHome.getHistoricalFiles().get(2).getRevision() == 1;
                assert docHome.getHistoricalFiles().get(0).getId().equals(6l);
                assert docHome.getHistoricalFiles().get(2).getLastModifiedByUsername().equals("guest");
                assert docHome.getHistoricalFiles().get(2).getLastModifiedOn() != null;

                assert docHome.getHistoricalFiles().get(3).getRevision() == 0;
                assert docHome.getHistoricalFiles().get(0).getId().equals(6l);
                assert docHome.getHistoricalFiles().get(3).getLastModifiedByUsername().equals("admin");
                assert docHome.getHistoricalFiles().get(3).getLastModifiedOn() == null;
            }

        }.run();
    }

    private void loginAdmin() throws Exception {
        new FacesRequest() {
           protected void invokeApplication() throws Exception {
              setValue("#{identity.username}", "admin");
              setValue("#{identity.password}", "admin");
              invokeAction("#{identity.login}");
              assert getValue("#{identity.loggedIn}").equals(true);
           }
        }.run();
    }

}
