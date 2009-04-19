/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.model.DisplayTagCount;
import org.jboss.seam.wiki.core.model.WikiDirectory;

import java.util.*;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("tagEditor")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class TagEditor implements Serializable {

    @In
    private TagDAO tagDAO;

    @In
    private WikiDirectory wikiRoot;

    private SortedSet<String> tags = new TreeSet<String>();
    private String newTag;
    private List<DisplayTagCount> popularTags;

    public SortedSet<String> getTags() {
        return tags;
    }

    public void setTags(SortedSet<String> tags) {
        this.tags = tags;
    }

    public List<String> getTagsAsList() {
        return tags != null ? new ArrayList<String>(tags) : Collections.EMPTY_LIST;
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void addNewTag() {
        if (!newTag.matches("[\\w\\s]+")) {
            StatusMessages.instance().addToControlFromResourceBundleOrDefault(
                "newTag",
                StatusMessage.Severity.WARN,
                "lacewiki.msg.tagEdit.TagCantContainSpecialCharacters",
                "Tag can only contain alphanumeric characters."
            );
        } else if (newTag.length() > 0) {
            tags.add(newTag);
            newTag = null;
        }
    }

    public List<DisplayTagCount> getPopularTags() {
        // Load 6 most popular tags
        if (popularTags == null) popularTags = tagDAO.findTagCounts(wikiRoot, null, 12, 1l);

        // Filter out the ones we already have
        List<DisplayTagCount> filtered = new ArrayList<DisplayTagCount>();
        for (DisplayTagCount popularTag : popularTags) {
            if (!tags.contains(popularTag.getTag()))
                filtered.add(popularTag);
        }
        return filtered;
    }

}
