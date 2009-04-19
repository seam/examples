/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.browse;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.List;

public class DisplayDocuments extends DBUnitSeamTest {


    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void documentFromNodeId() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", "6");
            }

            protected void renderResponse() throws Exception {
                WikiDocument doc = (WikiDocument)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(6l);
                
                assert doc.getArea().getId().equals(3l);

                WikiDirectory dir = (WikiDirectory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(3l);

                List<WikiNode> currentDirectoryPath = (List<WikiNode>)getValue("#{breadcrumb}");
                assert currentDirectoryPath.size() == 2;
                assert currentDirectoryPath.get(0).getId().equals( ((WikiDirectory)getValue("#{currentDirectory}")).getId() );
                assert currentDirectoryPath.get(1).getId().equals( ((WikiDocument)getValue("#{currentDocument}")).getId()  );

                assert getRenderedViewId().equals("/docDisplay_d.xhtml");
            }

        }.run();
    }

    @Test
    public void documentFromWikiName() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "CCC");
                setParameter("nodeName", "One");
            }

            protected void renderResponse() throws Exception {
                WikiDocument doc = (WikiDocument)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(6l);

                WikiDirectory dir = (WikiDirectory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(3l);

                assert getRenderedViewId().equals("/docDisplay_d.xhtml");
            }
        }.run();
    }

    @Test
    public void defaultDocumentFromAreaWikiName() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "CCC");
            }

            protected void renderResponse() throws Exception {
                WikiDocument doc = (WikiDocument)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(6l);

                WikiDirectory dir = (WikiDirectory)getValue("#{currentDirectory}");
                assert dir != null;
                assert dir.getId().equals(3l);

                assert getRenderedViewId().equals("/docDisplay_d.xhtml");
            }
        }.run();
    }

}
