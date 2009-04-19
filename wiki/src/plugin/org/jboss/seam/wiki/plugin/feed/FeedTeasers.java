package org.jboss.seam.wiki.plugin.feed;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.ScopeType;

import java.io.Serializable;
import java.util.List;

@Name("feedTeasers")
@Scope(ScopeType.PAGE)
public class FeedTeasers implements Serializable {

    public static final String MACRO_ATTR_TEASERLIST = "feedTeaserList";

    @In
    FeedDAO feedDAO;

    public List<FeedEntry> getTeasers(WikiPluginMacro macro) {
        List<FeedEntry> teaserList = (List<FeedEntry>)macro.getAttributes().get(MACRO_ATTR_TEASERLIST);
        if (teaserList == null) {
            FeedTeasersPreferences prefs = Preferences.instance().get(FeedTeasersPreferences.class, macro);
            teaserList =
                    feedDAO.findLastFeedEntries(
                        prefs.getFeed(),
                        prefs.getNumberOfTeasers().intValue()
                    );
            macro.getAttributes().put(MACRO_ATTR_TEASERLIST, teaserList);
        }
        return teaserList;
    }

}
