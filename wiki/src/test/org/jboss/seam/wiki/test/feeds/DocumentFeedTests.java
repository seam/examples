/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.feeds;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.core.ui.FeedServlet;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class DocumentFeedTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/DocumentFeedEntries.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test(groups="jdk6-expected-failures")
    public void createDocumentPushOnSiteFeed() throws Exception {

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

                docHome.setPushOnFeeds(true);
                docHome.setPushOnSiteFeed(true);

                assert invokeMethod("#{documentHome.persist}").equals("persisted");

            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {
                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);
                checkTestDocumentIsOnFeed("Anonymous Guest", "AAA", feedDAO.findFeed(1l), 1);
            }
        }.run();

    }

    @Test(groups="jdk6-expected-failures")
    public void updateDocumentPushOnSiteFeed() throws Exception {

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
                docHome.getInstance().setName("Test Name");
                docHome.getTextEditor().setValue("Test Content");

                docHome.setPushOnFeeds(true);
                docHome.setPushOnSiteFeed(true);

                assert invokeMethod("#{documentHome.update}").equals("updated");

            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {
                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);
                checkTestDocumentIsOnFeed("Regular Member", "AAA", feedDAO.findFeed(1l), 1);
            }
        }.run();

    }

    @Test(groups="jdk6-expected-failures")
    public void createDocumentPushOnParentDirectoryFeedOnly() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", "2");
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

                docHome.setPushOnFeeds(true);

                assert invokeMethod("#{documentHome.persist}").equals("persisted");

            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {
                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);
                checkTestDocumentIsOnFeed("Anonymous Guest", "BBB", feedDAO.findFeed(2l), 1);
                checkFeedHasEntries(feedDAO.findFeed(1l), 1);
            }
        }.run();

    }

    @Test
    public void updateDocumentPushOnParentDirectoryFeedOnly() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "9");
                setParameter("parentDirectoryId", "2");
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

                docHome.setPushOnFeeds(true);

                assert invokeMethod("#{documentHome.update}").equals("updated");

            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {
                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);
                checkTestDocumentIsOnFeed("System Administrator", "BBB", feedDAO.findFeed(2l), 0);
                checkFeedHasEntries(feedDAO.findFeed(1l), 1);
            }
        }.run();

    }

    @Test(groups="jdk6-expected-failures")
    public void createDocumentPushOnAllFeeds() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", "2");
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

                docHome.setPushOnFeeds(true);
                docHome.setPushOnSiteFeed(true);

                assert invokeMethod("#{documentHome.persist}").equals("persisted");

            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {
                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);
                checkTestDocumentIsOnFeed("Anonymous Guest", "AAA", feedDAO.findFeed(1l), 1);
                checkTestDocumentIsOnFeed("Anonymous Guest", "BBB", feedDAO.findFeed(2l), 1);
            }
        }.run();

    }

    @Test
    public void updateDocumentPushOnAllFeeds() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "9");
                setParameter("parentDirectoryId", "2");
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

                docHome.setPushOnFeeds(true);
                docHome.setPushOnSiteFeed(true);

                assert invokeMethod("#{documentHome.update}").equals("updated");

            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {
                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);
                checkTestDocumentIsOnFeed("System Administrator", "AAA", feedDAO.findFeed(1l), 0);
                checkTestDocumentIsOnFeed("System Administrator", "BBB", feedDAO.findFeed(2l), 0);
            }
        }.run();

    }

    @Test
    public void deleteDocumentRemoveFromFeeds() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("documentId", "9");
                setParameter("parentDirectoryId", "2");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert invokeMethod("#{documentHome.remove}").equals("removed");
            }
        }.run();

        new NonFacesRequest("/wiki.xhtml") {
            protected void renderResponse() throws Exception {
                FeedDAO feedDAO = (FeedDAO)getInstance(FeedDAO.class);
                checkFeedHasEntries(feedDAO.findFeed(1l), 0);
                checkFeedHasEntries(feedDAO.findFeed(2l), 0);
            }
        }.run();

    }

    private void checkTestDocumentIsOnFeed(String author, String feedTitle, Feed feed, int existingEntries) {
        FeedServlet feedServlet = new FeedServlet();
        SyndFeed syndFeed =
                feedServlet.createSyndFeed(
                    "http://foo.bar/atom.seam",
                    FeedServlet.SyndFeedType.ATOM,
                    feed,
                    0
                );
        assert syndFeed.getTitle().equals("LaceWiki - " +feedTitle);
        assert syndFeed.getEntries().size() == existingEntries + 1;
        assert ((SyndEntry)syndFeed.getEntries().get(0)).getTitle().equals("Test Name");
        assert ((SyndEntry)syndFeed.getEntries().get(0)).getAuthor().equals(author);
        assert ((SyndEntry)syndFeed.getEntries().get(0)).getDescription().getValue().equals("<p class=\"wikiPara\">\nTest Content</p>\n");
    }

    private void checkFeedHasEntries(Feed feed, int existingEntries) {
        FeedServlet feedServlet = new FeedServlet();
        SyndFeed syndFeed =
                feedServlet.createSyndFeed(
                    "http://foo.bar/atom.seam",
                    FeedServlet.SyndFeedType.ATOM,
                    feed,
                    0
                );
        assert syndFeed.getEntries().size() == existingEntries;
    }

}