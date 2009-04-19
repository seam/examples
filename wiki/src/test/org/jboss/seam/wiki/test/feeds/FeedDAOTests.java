/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.feeds;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiFeed;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

import java.util.List;

public class FeedDAOTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/DocumentFeedEntries.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void findFeeds() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {

                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDocument document = nodeDAO.findWikiDocument(9l);

                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);

                List<WikiFeed> feeds = feedDAO.findFeeds(document);
                assert feeds.size() == 2;
                assert feeds.get(0).getId().equals(2l);
                assert feeds.get(1).getId().equals(1l);
            }
        }.run();
    }

    @Test
    public void isOnSiteFeed() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {

                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDocument document = nodeDAO.findWikiDocument(9l);

                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);

                assert feedDAO.isOnSiteFeed(document);
            }
        }.run();
    }

    @Test
    public void findParentFeeds() throws Exception {
        new FacesRequest() {

            protected void invokeApplication() throws Exception {

                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory dir = nodeDAO.findWikiDirectory(2l);

                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);

                List<WikiFeed> feeds = feedDAO.findParentFeeds(dir, true);

                assert feeds.size() == 2;
                assert feeds.get(0).getId().equals(2l);
                assert feeds.get(1).getId().equals(1l);

            }
        }.run();
    }

}
