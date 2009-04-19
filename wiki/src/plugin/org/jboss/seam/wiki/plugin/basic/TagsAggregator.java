/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.basic;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.DisplayTagCount;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.Preferences;

import java.util.*;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("tagsAggregator")
@Scope(ScopeType.PAGE)
public class TagsAggregator implements Serializable {

    public static final String MACRO_ATTR_AGGREGATED_TAGS = "aggregatedTags";
    public static final String MACRO_ATTR_HIGHEST_TAG_COUNT = "highestTagCount";

    @In
    TagDAO tagDAO;

    @In
    WikiDirectory currentDirectory;

    @In
    WikiDocument currentDocument;

    public List<DisplayTagCount> getTagsSortedByCount(WikiPluginMacro macro) {

        List<DisplayTagCount> tagsSortedByCount = (List<DisplayTagCount>) macro.getAttributes().get(MACRO_ATTR_AGGREGATED_TAGS);
        if (tagsSortedByCount == null) {

            TagsPreferences prefs = Preferences.instance().get(TagsPreferences.class, macro);
            tagsSortedByCount =
                    tagDAO.findTagCounts(
                            currentDirectory,
                            currentDocument,
                            prefs.getMaxNumberOfTags() != null ? prefs.getMaxNumberOfTags().intValue() : 0,
                            prefs.getMinimumCount() != null ? prefs.getMinimumCount() : 1l
                    );

            macro.getAttributes().put(MACRO_ATTR_AGGREGATED_TAGS, tagsSortedByCount);
        }

        return tagsSortedByCount;
    }

    public Long getHighestTagCount(WikiPluginMacro macro) {
        Long highestTagCount = (Long)macro.getAttributes().get(MACRO_ATTR_HIGHEST_TAG_COUNT);
        if (highestTagCount == null) {
            highestTagCount = 0l;
            List<DisplayTagCount> tagsSortedByCount = getTagsSortedByCount(macro);
            for (DisplayTagCount tagCount : tagsSortedByCount) {
                if (tagCount.getCount() > highestTagCount) highestTagCount= tagCount.getCount();
            }
            macro.getAttributes().put(MACRO_ATTR_HIGHEST_TAG_COUNT, highestTagCount);
        }
        return highestTagCount;
    }


}
