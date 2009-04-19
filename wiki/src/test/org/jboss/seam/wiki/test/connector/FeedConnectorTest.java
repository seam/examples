/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.connector;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.connectors.feed.FeedAggregatorDAO;
import org.jboss.seam.wiki.connectors.feed.FeedEntryDTO;
import org.jboss.seam.wiki.connectors.feed.FeedConnector;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;
import org.testng.annotations.Test;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.net.URL;

/**
 * @author Christian Bauer
 */
public class FeedConnectorTest extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void getFeedDTOs() throws Exception {

        new NonFacesRequest() {
            protected void renderResponse() throws Exception {
                FeedAggregatorDAO dao = (FeedAggregatorDAO)getInstance(FeedAggregatorDAO.class);

                URL[] feedURLs = {
                    new URL("http://foo"), new URL("http://bar")
                };

                List<FeedEntryDTO> dtos = dao.getLatestFeedEntries(30, feedURLs);
                assert dtos.size() == 0; // Asynchronous cache needs to do its job first

                Thread.sleep(4000);

                dtos = dao.getLatestFeedEntries(30, feedURLs);
                assert dtos.size() == 3;

            }
        }.run();
    }

    @Name("feedConnector")
    @Scope(ScopeType.APPLICATION)
    @Install(precedence = Install.MOCK)
    @AutoCreate
    public static class MockFeedConnector implements FeedConnector {
        public List<FeedEntryDTO> getFeedEntries(String feedURL) {

            try {
                Thread.sleep(3000); // Wait 3 seconds...
            } catch (InterruptedException e) {}

            List<FeedEntryDTO> entries = new ArrayList<FeedEntryDTO>();

            if (feedURL.equals("http://foo")) {

                FeedEntry feOne = new FeedEntry();
                feOne.setTitle("One");
                feOne.setPublishedDate(new Date());

                FeedEntry feTwo = new FeedEntry();
                feTwo.setTitle("Two");
                feTwo.setPublishedDate(new Date());

                Feed feed = new Feed();
                feed.setTitle("Foo");
                feed.getFeedEntries().add(feOne);
                feed.getFeedEntries().add(feTwo);

                entries.add(new FeedEntryDTO(feed, feOne));
                entries.add(new FeedEntryDTO(feed, feTwo));

            } else if (feedURL.equals("http://bar")) {

                FeedEntry feOne = new FeedEntry();
                feOne.setTitle("One");
                feOne.setPublishedDate(new Date());

                Feed feed = new Feed();
                feed.setTitle("Bar");
                feed.getFeedEntries().add(feOne);

                entries.add(new FeedEntryDTO(feed, feOne));
            }

            return entries;

        }
    }

}
