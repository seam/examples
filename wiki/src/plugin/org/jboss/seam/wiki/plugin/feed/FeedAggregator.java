/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.feed;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.connectors.feed.FeedAggregatorDAO;
import org.jboss.seam.wiki.connectors.feed.FeedEntryDTO;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.Preferences;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("feedAggregator")
@Scope(ScopeType.PAGE)
public class FeedAggregator implements Serializable {

    public static final String MACRO_ATTR_FEEDENTRYLIST = "feedEntryList";

    @Logger
    Log log;

    @In
    FeedAggregatorDAO feedAggregatorDAO;

    public List<FeedEntryDTO> getFeedEntries(WikiPluginMacro macro) {
        List<FeedEntryDTO> feedEntries = (List<FeedEntryDTO>)macro.getAttributes().get(MACRO_ATTR_FEEDENTRYLIST);
        if (feedEntries == null) {
            FeedAggregatorPreferences prefs = Preferences.instance().get(FeedAggregatorPreferences.class, macro);
            if (prefs.getUrls() == null || prefs.getUrls().length() < 8) return null;

            List<URL> validURLs = getValidURLs(prefs.getUrls());
            log.debug("aggregating feeds: " + validURLs.size());

            String aggregateId =
                    prefs.getAggregateId() != null && prefs.getAggregateId().length() > 0
                        ? prefs.getAggregateId()
                        : null;

            if (aggregateId != null) {
                log.debug("aggregating under subscribable identifier: "+ aggregateId);
            }

            int numberOfEntries =
                    prefs.getNumberOfFeedEntries() != null ? prefs.getNumberOfFeedEntries().intValue() : 10;

            feedEntries =
                feedAggregatorDAO.getLatestFeedEntries(
                    numberOfEntries,
                    validURLs.toArray(new URL[validURLs.size()]),
                    aggregateId
                );
            macro.getAttributes().put(MACRO_ATTR_FEEDENTRYLIST, feedEntries);

        }
        return feedEntries;
    }

    private List<URL> getValidURLs(String spaceSeparatedURLs) {

        // Split the URLs by space
        String[] urls = spaceSeparatedURLs.split(" ");

        // First check if the URLs are valid, if not we might as well just skip it...
        List<URL> validUrls = new ArrayList<URL>();
        for (String url : urls) {
            try {
                URL u = new URL(url);
                if (!u.getProtocol().equals("http")) {
                    log.debug("skipping URL with unsupported protocol: " + url);
                    continue;
                }
                validUrls.add(u);
            } catch (MalformedURLException e) {
                log.debug("skipping invalid URL: " + url);
                continue;
            }
        }
        return validUrls;
    }

}
