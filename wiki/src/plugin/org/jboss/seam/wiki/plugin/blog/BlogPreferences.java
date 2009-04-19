/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.blog;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Preferences(
    name = "Blog",
    description = "#{messages['blog.preferences.description']}"
)
public class BlogPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['blog.directory.preferences.property.pageSize']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange",
        mappedTo = "blog.directory"
    )
    @Range(min = 3l, max = 25l)
    @NotNull
    private Long pageSize;

    @PreferenceProperty(
        description = "#{messages['blog.archive.preferences.property.archiveSubscribeIcon']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        mappedTo = "blog.archive"
    )
    @NotNull
    private Boolean archiveSubscribeIcon;

    @PreferenceProperty(
        description = "#{messages['blog.recentEntries.preferences.property.recentEntriesItems']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange",
        mappedTo = "blog.recentEntries"
    )
    @Range(min = 3l, max = 100l)
    @NotNull
    private Long recentEntriesItems;

    @PreferenceProperty(
        description = "#{messages['blog.recentEntries.preferences.property.recentEntriesTruncateTitle']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange",
        mappedTo = "blog.recentEntries"
    )
    @Range(min = 3l, max = 100l)
    @NotNull
    private Long recentEntriesTruncateTitle;

    @PreferenceProperty(
        description = "#{messages['blog.recentEntries.preferences.property.recentEntriesSubscribeIcon']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        mappedTo = "blog.recentEntries"
    )
    @NotNull
    private Boolean recentEntriesSubscribeIcon;

    public Long getPageSize() {
        return pageSize;
    }

    public Boolean getArchiveSubscribeIcon() {
        return archiveSubscribeIcon;
    }

    public Long getRecentEntriesItems() {
        return recentEntriesItems;
    }

    public Long getRecentEntriesTruncateTitle() {
        return recentEntriesTruncateTitle;
    }

    public Boolean getRecentEntriesSubscribeIcon() {
        return recentEntriesSubscribeIcon;
    }
}
