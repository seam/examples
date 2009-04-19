/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.core.model.FeedEntry;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
public class FeedEntryDTO implements Serializable {

    private Feed feed;
    private FeedEntry feedEntry;

    public FeedEntryDTO(Feed feed, FeedEntry feedEntry) {
        this.feed = feed;
        this.feedEntry = feedEntry;
    }

    public Feed getFeed() {
        return feed;
    }

    public FeedEntry getFeedEntry() {
        return feedEntry;
    }

    public String toString() {
        return "FeedEntryDTO - TITLE: '" + getFeedEntry().getTitle() + "' FEED: '" + getFeed().getTitle() + "'";
    }
}
