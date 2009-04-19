/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.browse;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.core.action.DirectoryBrowser;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DisplayDirectories extends DBUnitSeamTest {


    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void directoryFromAreaWikiname() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "BBB");
            }

            protected void renderResponse() throws Exception {
                WikiDocument doc = (WikiDocument)getValue("#{currentDocument}");
                assert doc == null;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                WikiDirectory dir = browser.getInstance();
                assert dir != null;
                assert dir.getId().equals(2l);
                assert dir.getArea().getId().equals(2l);

                assert browser.getChildNodes().size() == 1;

                assert browser.isRemovable(browser.getChildNodes().get(0));

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");
            }
        }.run();
    }

    @Test
    public void directoryFromFullWikiname() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("areaName", "CCC");
                setParameter("nodeName", "DDD");
            }

            protected void renderResponse() throws Exception {
                WikiDocument doc = (WikiDocument)getValue("#{currentDocument}");
                assert doc == null;

                DirectoryBrowser browser = (DirectoryBrowser)getInstance(DirectoryBrowser.class);
                WikiDirectory dir = browser.getInstance();
                assert dir != null;
                assert dir.getId().equals(4l);

                assert browser.getChildNodes().size() == 1;

                assert browser.isRemovable(browser.getChildNodes().get(0));

                assert getRenderedViewId().equals("/dirDisplay_d.xhtml");
            }
        }.run();
    }

}
