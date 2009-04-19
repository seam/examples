package org.jboss.seam.wiki.plugin.feed;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.hibernate.validator.Length;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Preferences(
    name = "FeedTeasers",
    description = "#{messages['feed.teasers.preferences.description']}",
    mappedTo = "feed.teasers"
)
public class FeedTeasersPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['feed.teasers.preferences.property.Title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['feed.teasers.preferences.property.feed']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "SelectOne",
        templateComponentName = "feedTeasersFeedPreferenceValueTemplate"
    )
    private Long feed;

    @PreferenceProperty(
        description = "#{messages['feed.teasers.preferences.property.numberOfTeasers']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 25l)
    @NotNull
    private Long numberOfTeasers;

    @PreferenceProperty(
        description = "#{messages['feed.teasers.preferences.property.truncateDescription']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 10l, max = 5000l)
    @NotNull
    private Long truncateDescription;

    @PreferenceProperty(
        description = "#{messages['feed.teasers.preferences.property.showAuthor']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE}
    )
    @NotNull
    private Boolean showAuthor;

    public String getTitle() {
        return title;
    }

    public Long getFeed() {
        return feed;
    }

    public Long getNumberOfTeasers() {
        return numberOfTeasers;
    }

    public Long getTruncateDescription() {
        return truncateDescription;
    }

    public Boolean getShowAuthor() {
        return showAuthor;
    }
}
