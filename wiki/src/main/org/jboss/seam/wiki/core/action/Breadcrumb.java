package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.cache.PageFragmentCache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Name("breadcrumbFactory")
@Scope(ScopeType.PAGE)
public class Breadcrumb implements Serializable {

    public static final String CACHE_REGION = "wiki.Breadcrumb";
    public static final String CACHE_KEY = "BreadcrumbOfNodeId";

    @Logger
    Log log;

    @In(required = false)
    WikiNode currentLocation;

    @Factory(value = "breadcrumb", autoCreate = true)
    public List<WikiNode> unwrap() {
        // TODO: Maybe a nested set query would be more efficient?
        log.debug("breadcrumb starting at current location: " + currentLocation);
        List<WikiNode> currentPath = new ArrayList<WikiNode>();
        if (currentLocation == null) return currentPath;
        addToPath(currentPath, currentLocation);
        Collections.reverse(currentPath);
        return currentPath;
    }

    protected void addToPath(List<WikiNode> path, WikiNode currentLocation) {
        if (Identity.instance().hasPermission("Node", "read", currentLocation) &&
            currentLocation.getId() != null && !isRootWikiNode(currentLocation) ) {
            log.debug("adding to breadcrumb: " + currentLocation);
            path.add(currentLocation);

        }
        if (currentLocation.getParent() != null ) {
            addToPath(path, currentLocation.getParent());
        }
    }

    private boolean isRootWikiNode(WikiNode node) {
        return (node.isInstance(WikiDirectory.class) && node.getId().equals(((WikiDirectory) Component.getInstance("wikiRoot")).getId()));
    }

    public boolean isCacheEnabled() {
        return false;
        // TODO: Cache disabled, needs to consider year/month/day and tag request parameters in cache key
        //return currentLocation != null;
    }

    public String getCacheKey() {
        return currentLocation != null ? CACHE_KEY + currentLocation.getId().toString() : null;
    }

    public String getCacheRegion() {
        return CACHE_REGION;
    }

    @Observer(value = { "Node.updated"})
    public void invalidateCache(WikiNode node) {
        log.debug("invalidating cached item: " + CACHE_KEY +node.getId());
        PageFragmentCache.instance().remove(CACHE_REGION, CACHE_KEY +node.getId());
    }

}
