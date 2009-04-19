/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.plugin;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;

public class BlogDAOTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/BlogData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }
/*
    @Test
    public void findBlogEntriesWithCommentCount() throws Exception {
        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory blogDir = nodeDAO.findWikiDirectory(51l);

                BlogDAO dao = (BlogDAO)getInstance("blogDAO");

                List<BlogEntry> entries =
                    dao.findBlogEntriesWithCommentCount(
                            blogDir,
                            (WikiDocument)blogDir.getDefaultFile(),
                            "createdOn", true,
                            0, 10,
                            null, null, null,
                            null
                    );
                assert entries.size() == 5;
                assert entries.get(0).getEntryDocument().getId().equals(55l);
                assert entries.get(0).getCommentCount().equals(2l);
                assert entries.get(1).getEntryDocument().getId().equals(54l);
                assert entries.get(1).getCommentCount().equals(1l);
                assert entries.get(2).getEntryDocument().getId().equals(53l);
                assert entries.get(2).getCommentCount().equals(0l);
                assert entries.get(3).getEntryDocument().getId().equals(52l);
                assert entries.get(3).getCommentCount().equals(0l);
                assert entries.get(4).getEntryDocument().getId().equals(51l);
                assert entries.get(4).getCommentCount().equals(0l);
            }
        }.run();

    }

    @Test
    public void findBlogEntriesWithCommentCountLimitDate() throws Exception {
        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory blogDir = nodeDAO.findWikiDirectory(51l);

                BlogDAO dao = (BlogDAO)getInstance("blogDAO");

                // Year
                List<BlogEntry> entries =
                    dao.findBlogEntriesWithCommentCount(
                            blogDir,
                            (WikiDocument)blogDir.getDefaultFile(),
                            "createdOn", true,
                            0,10,
                            2007, null, null,
                            null
                    );
                assert entries.size() == 4;
                assert entries.get(0).getEntryDocument().getId().equals(55l);
                assert entries.get(0).getCommentCount().equals(2l);
                assert entries.get(1).getEntryDocument().getId().equals(54l);
                assert entries.get(1).getCommentCount().equals(1l);
                assert entries.get(2).getEntryDocument().getId().equals(53l);
                assert entries.get(2).getCommentCount().equals(0l);
                assert entries.get(3).getEntryDocument().getId().equals(52l);
                assert entries.get(3).getCommentCount().equals(0l);

                // Month
                entries =
                    dao.findBlogEntriesWithCommentCount(
                            blogDir,
                            (WikiDocument)blogDir.getDefaultFile(),
                            "createdOn", true,
                            0,10,
                            2007, 8, null,
                            null
                    );
                assert entries.size() == 2;
                assert entries.get(0).getEntryDocument().getId().equals(53l);
                assert entries.get(0).getCommentCount().equals(0l);
                assert entries.get(1).getEntryDocument().getId().equals(52l);
                assert entries.get(1).getCommentCount().equals(0l);

                // Day
                entries =
                    dao.findBlogEntriesWithCommentCount(
                            blogDir,
                            (WikiDocument)blogDir.getDefaultFile(),
                            "createdOn", true,
                            0, 10,
                            2007, 9, 2,
                            null
                    );
                assert entries.size() == 2;
                assert entries.get(0).getEntryDocument().getId().equals(55l);
                assert entries.get(0).getCommentCount().equals(2l);
                assert entries.get(1).getEntryDocument().getId().equals(54l);
                assert entries.get(1).getCommentCount().equals(1l);

            }
        }.run();

    }

    @Test
    public void findBlogEntriesWithCommentCountLimitTag() throws Exception {
        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory blogDir = nodeDAO.findWikiDirectory(51l);

                BlogDAO dao = (BlogDAO)getInstance("blogDAO");

                // Year
                List<BlogEntry> entries =
                    dao.findBlogEntriesWithCommentCount(
                            blogDir,
                            (WikiDocument)blogDir.getDefaultFile(),
                            "createdOn", true,
                            0,10,
                            null, null, null,
                            "foo"
                    );

                assert entries.size() == 3;
                assert entries.get(0).getEntryDocument().getId().equals(55l);
                assert entries.get(0).getCommentCount().equals(2l);
                assert entries.get(1).getEntryDocument().getId().equals(52l);
                assert entries.get(1).getCommentCount().equals(0l);
                assert entries.get(2).getEntryDocument().getId().equals(51l);
                assert entries.get(2).getCommentCount().equals(0l);
            }
        }.run();
    }

    @Test
    public void findBlogEntriesWithCommentCountLimitTagDate() throws Exception {
        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory blogDir = nodeDAO.findWikiDirectory(51l);

                BlogDAO dao = (BlogDAO)getInstance("blogDAO");

                // Year
                List<BlogEntry> entries =
                    dao.findBlogEntriesWithCommentCount(
                            blogDir,
                            (WikiDocument)blogDir.getDefaultFile(),
                            "createdOn", true,
                            0,10,
                            2007, null, null,
                            "foo"
                    );

                assert entries.size() == 2;
                assert entries.get(0).getEntryDocument().getId().equals(55l);
                assert entries.get(0).getCommentCount().equals(2l);
                assert entries.get(1).getEntryDocument().getId().equals(52l);
                assert entries.get(1).getCommentCount().equals(0l);
            }
        }.run();
    }
    */

}
