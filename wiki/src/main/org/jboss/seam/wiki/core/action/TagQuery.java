/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.exception.InvalidWikiRequestException;
import org.jboss.seam.wiki.core.cache.PageFragmentCache;

import java.io.Serializable;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("tagQuery")
@Scope(ScopeType.CONVERSATION)
public class TagQuery implements Serializable {

    public static final String CACHE_REGION = "wiki.TagList";
    public static final String CACHE_KEY = "TagsForDirectory";

    @Logger
    Log log;

    @In
    TagDAO tagDAO;

    @In
    WikiDirectory wikiRoot;

    private String tag;
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    private List<WikiFile> taggedFiles;

    public List<WikiFile> getTaggedFiles() {
        if (taggedFiles == null) {
            loadTaggedFiles();
        }
        return taggedFiles;
    }

    public void loadTaggedFiles() {
        if (tag == null) {
            throw new InvalidWikiRequestException("Missing tag parameter");
        }
        log.debug("loading wiki files tagged with: " + tag);
        taggedFiles = tagDAO.findWikFiles(wikiRoot, null, tag, WikiNode.SortableProperty.createdOn, false);
    }

    List<DisplayTagCount> tagsSortedByCount;
    Long highestTagCount;

    public List<DisplayTagCount> getTagsSortedByCount(int maxNumberOfTags, int minimumCount) {
        if (tagsSortedByCount == null) {
            WikiDirectory currentDirectory = (WikiDirectory) Component.getInstance("currentDirectory");
            tagsSortedByCount = tagDAO.findTagCounts(currentDirectory, null, maxNumberOfTags, minimumCount);
        }
        return tagsSortedByCount;
    }

    public Long getHighestTagCount(int maxNumberOfTags, int minimumCount) {
        if (highestTagCount == null) {
            highestTagCount = 0l;
            List<DisplayTagCount> tagsSortedByCount = getTagsSortedByCount(maxNumberOfTags, minimumCount);
            for (DisplayTagCount tagCount : tagsSortedByCount) {
                if (tagCount.getCount() > highestTagCount) highestTagCount= tagCount.getCount();
            }
        }
        return highestTagCount;
    }

    public String getCacheRegion() {
        return CACHE_REGION;
    }

    public String getCacheKey(int maxNumberOfTags, int minimumCount) {
        Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
        WikiDirectory currentDirectory = (WikiDirectory) Component.getInstance("currentDirectory");
        return CACHE_KEY + currentDirectory.getId() + "_" + maxNumberOfTags + "_" + minimumCount + "_" + currentAccessLevel;
    }

    @Observer(value = { "Node.updated", "Node.removed", "Node.persisted"}, create = true)
    public void invalidateCache() {
        PageFragmentCache.instance().removeAll(CACHE_REGION);
    }

}
