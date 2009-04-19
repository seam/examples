/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.blog;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.action.Pager;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.util.Hash;
import java.io.Serializable;
import java.util.*;

/**
 * @author Christian Bauer
 */
@Name("blogDirectory")
@Scope(ScopeType.CONVERSATION)
public class BlogDirectory implements Serializable {

    @Logger
    Log log;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    BlogDAO blogDAO;

    @In
    WikiDirectory currentDirectory;

    @In
    WikiDocument currentDocument;

    private Pager pager;
    private Integer page;
    private Integer year;
    private Integer month;
    private Integer day;
    private String tag;
    private Long numberOfEntriesForCurrentTag;

    @RequestParameter
    public void setPage(Integer page) {
        this.page = page;
    }

    @RequestParameter
    public void setYear(Integer year) {
        this.year = year;
    }
    @RequestParameter
    public void setMonth(Integer month) {
        this.month = month;
    }
    @RequestParameter
    public void setDay(Integer day) {
        this.day = day;
    }
    @RequestParameter
    public void setTag(String tag) {
        this.tag = tag;
    }

    /* ############################### BLOG ENTRIES ############################### */

    private List<BlogEntry> blogEntries;

    public List<BlogEntry> getBlogEntries(WikiPluginMacro macro) {
        BlogPreferences prefs = Preferences.instance().get(BlogPreferences.class, macro);

        if (pager == null) {
            log.debug("creating new pager with page size from macro preferences: " + prefs.getPageSize());
            pager = new Pager(prefs.getPageSize());
            pager.setPage(page);
        }

        if (blogEntries == null || !pager.getPageSize().equals(prefs.getPageSize())) {
            log.debug("blog entries list is null or pager is outdated, loading blog entries");
            pager.setPageSize(prefs.getPageSize());
            loadBlogEntries();
        }
        return blogEntries;
    }

    public void loadBlogEntries() {
        if (pager == null) throw new IllegalStateException("Need to call getBlogEntries(currentMacro) first!");

        log.debug("loading blog entries with existing: " + pager);

        pager.setNumOfRecords(
            blogDAO.countBlogEntries(currentDirectory, currentDocument, year, month, day, tag)
        );

        // Don't do the expensive query if we got no records
        if (pager.getNumOfRecords() == 0) {
            blogEntries = Collections.EMPTY_LIST;
            return;
        }

        blogEntries =
            blogDAO.findBlogEntriesInDirectory(
                    currentDirectory, currentDocument,
                    pager, year, month, day, tag,
                    true
            );
    }

    public Pager getPager() {
        return pager;
    }

    public Long getNumberOfEntriesForCurrentTag() {
        if (numberOfEntriesForCurrentTag == null) {
            numberOfEntriesForCurrentTag = blogDAO.countBlogEntries(currentDirectory, currentDocument, null, null, null, tag);
        }
        return numberOfEntriesForCurrentTag;
    }

    /* ################################ BLOG ARCHIVE ############################################ */

    private List<BlogEntryCount> archivedEntries;

    public List<BlogEntryCount> getArchivedEntries() {
        if (archivedEntries == null) loadArchivedEntries();
        return archivedEntries;
    }

    public void loadArchivedEntries() {
        log.debug("loading blog entries and counting/aggregating them by year and month");
        archivedEntries =
                blogDAO.countAllBlogEntriesGroupByYearMonth(currentDirectory, currentDocument, tag);
        log.debug("archived entries: " + archivedEntries.size());
    }

    public String getArchiveCacheKeyAppendix() {
        StringBuilder builder = new StringBuilder();
        if (tag != null && Math.abs(tag.hashCode()) != 0) builder.append(Math.abs(tag.hashCode()));
        builder.append(WikiUtil.dateAsString(year, month, day));
        Hash hash = (Hash)Component.getInstance(Hash.class);
        return hash.hash(builder.toString());
    }

    /* ################################ BLOG RECENT ENTRIES ############################################ */

    // Need to expose this as a datamodel so Seam can convert our map to a collection of Map.Entry objects
    @DataModel
    private Map<Date, List<BlogEntry>> recentBlogEntries;

    @Factory(value = "recentBlogEntries")
    public void loadRecentBlogEntries() {
        // TODO: This is supposed to use the currentMacro parameter to get the INSTANCE prefs value... how?
        BlogPreferences prefs = Preferences.instance().get(BlogPreferences.class);
        List<BlogEntry> recentBlogEntriesNonAggregated =
            blogDAO.findBlogEntriesInDirectory(
                    currentDirectory,
                    currentDocument,
                    new Pager(prefs.getRecentEntriesItems()),
                    null, null, null,
                    null, false
            );

        // Now aggregate by day
        recentBlogEntries = new LinkedHashMap<Date, List<BlogEntry>>();
        for (BlogEntry blogEntry : recentBlogEntriesNonAggregated) {

            // Find the day (ignore the hours, minutes, etc.)
            Calendar createdOn = new GregorianCalendar();
            createdOn.setTime(blogEntry.getEntryDocument().getCreatedOn());
            GregorianCalendar createdOnDay = new GregorianCalendar(
                createdOn.get(Calendar.YEAR), createdOn.get(Calendar.MONTH), createdOn.get(Calendar.DAY_OF_MONTH)
            );
            Date createdOnDate = createdOnDay.getTime(); // Jesus, this API is just bad...

            // Aggregate by day
            List<BlogEntry> entriesForDay =
                recentBlogEntries.containsKey(createdOnDate)
                ? recentBlogEntries.get(createdOnDate)
                : new ArrayList<BlogEntry>();

            entriesForDay.add(blogEntry);
            recentBlogEntries.put(createdOnDate, entriesForDay);
        }
    }

    public String getDateUrl() {
        return WikiUtil.dateAsString(year, month, day);
    }

    public String getTagUrl() {
        return tag != null && tag.length()>0 ? "/Tag/" + WikiUtil.encodeURL(tag) : "";
    }

}
