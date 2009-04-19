package org.jboss.seam.wiki.core.wikitext.engine;

import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.wikitext.renderer.WikiTextRenderer;
import org.jboss.seam.wiki.util.WikiUtil;

/**
 * Simple value holder for link resolution and rendering.
 *
 * @author Christian Bauer
 */
public class WikiLink {

    int identifier;
    WikiFile file;
    boolean requiresUpdating = false;
    String url;
    String fragment;
    String description;
    boolean broken = false;
    boolean external = false;

    public WikiLink(boolean broken, boolean external) {
        this.broken = broken;
        this.external = external;
    }

    public int getIdentifier() { return identifier; }
    public void setIdentifier(int identifier) { this.identifier = identifier; }

    public WikiFile getFile() { return file; }
    public void setFile(WikiFile file) { this.file = file; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getFragment() { return fragment; }
    public void setFragment(String fragment) { this.fragment = fragment; }

    public String getEncodedFragment() {
        if (fragment != null) {
            return WikiTextRenderer.HEADLINE_ID_PREFIX+WikiUtil.convertToWikiName(fragment);
        }
        return "";
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isBroken() { return broken; }
    public boolean isExternal() { return external; }

    public boolean isRequiresUpdating() { return requiresUpdating; }
    public void setRequiresUpdating(boolean requiresUpdating) { this.requiresUpdating = requiresUpdating; }

    public String toString() {
        return "File:" + file + " Description: " + description + " URL: " + url;
    }
}
