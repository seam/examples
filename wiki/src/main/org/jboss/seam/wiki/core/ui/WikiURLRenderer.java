/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.core.ui.FeedServlet;

import java.io.Serializable;

/**
 * Renders outgoing URLs in a unified fashion, see urlrewrite.xml for incoming URL GET request rewriting.
 * <p>
 * Note that some of the rendering is delegated into the domain model for subclasses of <tt>WikiNode</tt>.
 * </p>
 *
 * @author Christian Bauer
 */

@Name("wikiURLRenderer")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class WikiURLRenderer implements Serializable {

    @Logger
    Log log;

    @In
    String contextPath;

    @In("#{preferences.get('Wiki')}")
    WikiPreferences prefs;

    public String renderSearchURL(String search) {
        return renderSearchURL(search, false);
    }

    public String renderSearchURL(String search, boolean usePrefsPath) {
        if (search == null || search.length() == 0) return "";
        StringBuilder url = new StringBuilder();
        String skin = Component.getInstance("skin") != null ? (String)Component.getInstance("skin") : "d";
        url.append(usePrefsPath ? prefs.getBaseUrl() : contextPath);
        url.append("/search_").append(skin).append(".seam?query=").append(encodeURL(search));
        return url.toString();
    }

    public String renderTagURL(String tag) {
        return renderTagURL(tag, false);
    }

    public String renderTagURL(String tag, boolean usePrefsPath) {
        if (tag == null || tag.length() == 0) return "";
        StringBuilder url = new StringBuilder();
        url.append(usePrefsPath ? prefs.getBaseUrl() : contextPath);
        url.append("/tag/").append(encodeURL(tag));
        return url.toString();
    }

    public String renderUserProfileURL(User user) {
        return renderUserProfileURL(user, false);
    }

    public String renderUserProfileURL(User user, boolean usePrefsPath) {
        if (user == null || user.getUsername() == null) return "";
        StringBuilder url = new StringBuilder();
        url.append(usePrefsPath ? prefs.getBaseUrl() : contextPath);
        url.append("/user/").append(user.getUsername());
        return url.toString();
    }

    public String renderUserPortraitURL(User user, boolean small) {
        return renderUserPortraitURL(user, small, false);
    }

    public String renderUserPortraitURL(User user, boolean small, boolean usePrefsPath) {
        if (user == null || user.getId() == null) return "";
        StringBuilder url = new StringBuilder();
        if (usePrefsPath) url.append(prefs.getBaseUrl());
        url.append("/seam/resource/wikiUserPortrait/").append(user.getId()).append("/").append(small ? "s" : "l");
        return url.toString();
    }

    public String renderAggregateFeedURL(String aggregateId) {
        return renderAggregateFeedURL(aggregateId, false);
    }

    public String renderAggregateFeedURL(String aggregateId, boolean usePrefsPath) {
        if (aggregateId == null) return "";
        StringBuilder url = new StringBuilder();
        url.append(usePrefsPath ? prefs.getBaseUrl() : contextPath);
        url.append("/service/Feed/atom/Aggregate/").append(aggregateId);
        return url.toString();
    }

    public String renderFeedURL(Feed feed) {
        return renderFeedURL(feed, null, null, false);
    }

    public String renderFeedURL(Feed feed, String tag, String comments) {
        return renderFeedURL(feed, tag, comments, false);
    }

    public String renderFeedURL(Feed feed, String tag, String comments, boolean usePrefsPath) {
        if (feed == null || feed.getId() == null) return "";
        StringBuilder url = new StringBuilder();
        url.append(usePrefsPath ? prefs.getBaseUrl() : contextPath);
        url.append("/service/Feed/atom").append(feed.getURL());
        if (comments != null && comments.length() >0) {
            url.append("/Comments/").append(FeedServlet.Comments.valueOf(comments));
        }
        if (tag != null && tag.length() >0) {
            url.append("/Tag/").append(encodeURL(tag));
        }
        return url.toString();
    }

    public String renderURL(WikiNode node) {
        return renderURL(node, false);
    }

    public String renderURL(WikiNode node, boolean usePrefsPath) {
        if (node == null || node.getId() == null) return "";
        return prefs.isRenderPermlinks() ? renderPermURL(node, usePrefsPath) : renderWikiURL(node, usePrefsPath);
    }

    public String renderPermURL(WikiNode node) {
        return renderPermURL(node, false);
    }

    public String renderPermURL(WikiNode node, boolean usePrefsPath) {
        log.debug("rendering perm URL for node: " + node);
        if (node == null || node.getId() == null) return "";
        String url = (usePrefsPath ? prefs.getBaseUrl() : contextPath) + "/" + node.getPermURL(prefs.getPermlinkSuffix());
        log.debug("rendered URL: " + url);
        return url;
    }

    public String renderWikiURL(WikiNode node) {
        return renderWikiURL(node, false);
    }

    public String renderWikiURL(WikiNode node, boolean usePrefsPath) {
        log.debug("rendering wiki URL for node: " + node);
        if (node == null || node.getId() == null) return "";
        String url = (usePrefsPath ? prefs.getBaseUrl() : contextPath) + "/" + node.getWikiURL();
        log.debug("rendered URL: " + url);
        return url;
    }

    // TODO: We need more methods here, rendering year/month/day/tag/etc. on WikiURL (not perm url)

    private String encodeURL(String s) {
        return WikiUtil.encodeURL(s);
    }

    public static WikiURLRenderer instance() {
        return (WikiURLRenderer) Component.getInstance(WikiURLRenderer.class);
    }

}
