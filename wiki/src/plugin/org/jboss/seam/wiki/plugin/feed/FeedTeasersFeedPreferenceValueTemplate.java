/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.feed;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.preferences.PreferenceValueTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("feedTeasersFeedPreferenceValueTemplate")
@Scope(ScopeType.CONVERSATION)
public class FeedTeasersFeedPreferenceValueTemplate implements PreferenceValueTemplate, Serializable {

    @In
    FeedDAO feedDAO;

    List<String> feedIdentifiers;

    public List<String> getTemplateValues() {
        if (feedIdentifiers == null) {
            List<Feed> feeds = feedDAO.findAllFeeds();
            for (Feed feed : feeds) {
                feedIdentifiers.add(feed.getId().toString());
            }
        }
        return feedIdentifiers;
    }

}
