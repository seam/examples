/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.plugin;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.plugin.faqBrowser.FaqBrowserDAO;
import org.testng.annotations.Test;

/**
 * @author Christian Bauer
 */
public class FaqBrowserDAOTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/FAQData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void findFaqRoot() throws Exception {
        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);

                FaqBrowserDAO dao = (FaqBrowserDAO)getInstance(FaqBrowserDAO.class);

                // Start in a subdirectory of the FAQ root
                WikiDirectory faqSubDir = nodeDAO.findWikiDirectory(302l);
                WikiDirectory root = dao.findFaqRootDir(faqSubDir);
                assert root.getId().equals(300l);

                // Start in the root directory (should be the same)
                WikiDirectory faqDir = nodeDAO.findWikiDirectory(300l);
                root = dao.findFaqRootDir(faqDir);
                assert root.getId().equals(300l);

            }
        }.run();
    }
}
