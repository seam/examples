/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.feed;

import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.hibernate.validator.Pattern;

/**
 * @author Christian Bauer
 */
@Preferences(
    name = "FeedAggregator",
    description = "#{messages['feed.aggregator.preferences.description']}",
    mappedTo = "feed.aggregator"
)
public class FeedAggregatorPreferences {

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.urls']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 4096)
    private String urls;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.numberOfFeedEntries']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 99l)
    @NotNull
    private Long numberOfFeedEntries;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.truncateDescription']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 10l, max = 1000l)
    @NotNull
    private Long truncateDescription;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.aggregateId']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Pattern(regex="^[a-zA-Z0-9]+[a-zA-Z0-9\\s]*")
    @Length(min = 0, max = 255)
    private String aggregateId;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.hideDate']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideDate;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.hideAuthor']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideAuthor;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.hideFeedInfo']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideFeedInfo;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.hideDescription']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideDescription;

    @PreferenceProperty(
        description = "#{messages['feed.aggregator.preferences.property.hideTitle']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideTitle;

    public String getTitle() {
        return title;
    }

    public String getUrls() {
        return urls;
    }

    public Long getNumberOfFeedEntries() {
        return numberOfFeedEntries;
    }

    public Long getTruncateDescription() {
        return truncateDescription;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public Boolean getHideDate() {
        return hideDate;
    }

    public Boolean getHideAuthor() {
        return hideAuthor;
    }

    public Boolean getHideFeedInfo() {
        return hideFeedInfo;
    }

    public Boolean getHideDescription() {
        return hideDescription;
    }

    public Boolean getHideTitle() {
        return hideTitle;
    }
}
