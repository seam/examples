/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.browse;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.DocumentHistory;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.List;

// TODO: Finish this, http://jira.jboss.com/jira/browse/JBSEAM-2296
public class DisplayHistory extends DBUnitSeamTest {


    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void displayDocumentHistory() throws Exception {

        new FacesRequest("/docHistory_d.xhtml") {

            protected void beforeRequest() {
                setParameter("fileId", "6");
            }

            protected void invokeApplication() throws Exception {
                DocumentHistory docHistory = (DocumentHistory)getInstance(DocumentHistory.class);
                docHistory.init();
            }

            protected void renderResponse() throws Exception {
                WikiFile currentFile = (WikiFile)getValue("#{documentHistory.currentFile}");
                assert currentFile.getId().equals(6l);
                assert currentFile.getRevision() == 3;

                DocumentHistory docHistory = (DocumentHistory)getInstance(DocumentHistory.class);
                List<WikiFile> historicalFileList = docHistory.getHistoricalFileList();
                assert historicalFileList.size() == 3;

                // Sorted by revision descending
                assert historicalFileList.get(0).getRevision() == 2;
                assert historicalFileList.get(0).getId().equals(6l);
                assert historicalFileList.get(0).getLastModifiedByUsername().equals("admin");
                assert historicalFileList.get(0).getLastModifiedOn() != null;

                assert historicalFileList.get(1).getRevision() == 1;
                assert historicalFileList.get(0).getId().equals(6l);
                assert historicalFileList.get(1).getLastModifiedByUsername().equals("guest");
                assert historicalFileList.get(1).getLastModifiedOn() != null;

                assert historicalFileList.get(2).getRevision() == 0;
                assert historicalFileList.get(0).getId().equals(6l);
                assert historicalFileList.get(2).getLastModifiedByUsername().equals("admin");
                assert historicalFileList.get(2).getLastModifiedOn() == null;

            }
        }.run();
    }

    //@Test
    // TODO: Seam bug http://jira.jboss.com/jira/browse/JBSEAM-2296
    public void displayHistoricalRevision() throws Exception {

        new FacesRequest("/docHistory_d.xhtml") {

            protected void beforeRequest() {
                setParameter("fileId", "1");
            }

            protected void invokeApplication() throws Exception {
                DocumentHistory docHistory = (DocumentHistory)getInstance(DocumentHistory.class);
                docHistory.init();

                List<WikiFile> historicalFileList = docHistory.getHistoricalFileList();
                WikiFile selected = historicalFileList.get(1);
                assert selected.getHistoricalFileId().equals(2l);

                docHistory.setSelectedHistoricalFile(selected);
                WikiFile selectedHistoricalFile = docHistory.getSelectedHistoricalFile();

                assert selectedHistoricalFile.getHistoricalFileId().equals(selected.getHistoricalFileId());
                assert selectedHistoricalFile.getId().equals(1l);
                assert selectedHistoricalFile.getRevision() == 1;
            }

            protected void renderResponse() throws Exception {
                DocumentHistory docHistory = (DocumentHistory)getInstance(DocumentHistory.class);

            }
        }.run();
    }

    //@Test
    public void test() throws Exception {
        new FacesRequest() {
            /*
            protected void invokeApplication() throws Exception {
                TestBean testBean = (TestBean)getInstance("testBean");
                testBean.init();
                testBean.setName("B");
                System.out.println("#### NAME IS: " + testBean.getName());
                assert testBean.getName().equals("B");
            }
            */
        }.run();
    }

}
