package org.jboss.seam.wiki.core.search;

/**
 * Represents a single search result, used for rendering a hit in the UI.
 * <p>
 * This is a value holder that is build by the search engine and rendered by the
 * search user interface. <b>Important:</b> The title and fragment is rendered
 * <i>as is</i>, with no escaping of dangerous HTML! This is required because the
 * fragments might contain HTML markup that represents the hit highlights.
 * You need to absolutely make sure that these values do not contain any Javascript
 * or your site will be open for XSS attacks. Use <tt>WikiUtil.escapeHtml(s)</tt>
 * as a helper method.
 *
 * @author Christian Bauer
 */
public class SearchHit {

    public String type;
    public String icon;
    public String title;
    public String link;
    public String fragment;

    public SearchHit() {}

    public SearchHit(String type, String icon, String title, String link, String fragment) {
        this.type = type;
        this.icon = icon;
        this.title = title;
        this.link = link;
        this.fragment = fragment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getFragment() {
        return fragment;
    }

    public void setFragment(String fragment) {
        this.fragment = fragment;
    }
}
