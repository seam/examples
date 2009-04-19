/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.browse;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.CommentQuery;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DisplayComments extends DBUnitSeamTest {


    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void commentQuery() throws Exception {

        new NonFacesRequest("/wiki.xhtml") {

            protected void beforeRequest() {
                setParameter("nodeId", "6");
            }

            protected void renderResponse() throws Exception {
                WikiDocument doc = (WikiDocument)getValue("#{currentDocument}");
                assert doc != null;
                assert doc.getId().equals(6l);


                CommentQuery commentQuery = (CommentQuery)getInstance(CommentQuery.class);
                assert commentQuery.getComments().size() == 6;

                assert commentQuery.getComments().get(0).getId().equals(10l);
                assert commentQuery.getComments().get(1).getId().equals(11l);
                assert commentQuery.getComments().get(2).getId().equals(12l);
                assert commentQuery.getComments().get(3).getId().equals(13l);
                assert commentQuery.getComments().get(4).getId().equals(14l);
                assert commentQuery.getComments().get(5).getId().equals(15l);
            }

        }.run();
    }


}
