/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.blog;

import org.jboss.seam.wiki.util.WikiUtil;

/**
 * @author Christian Bauer
 */
public class BlogEntryCount {

    Long numOfEntries;
    Integer year;
    Integer month;
    Integer day;

    public BlogEntryCount() {}

    public Long getNumOfEntries() {
        return numOfEntries;
    }

    public void setNumOfEntries(Long numOfEntries) {
        this.numOfEntries = numOfEntries;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public String getAsString() {
        return WikiUtil.dateAsString(year, month, day);
    }

    public String toString() {
        return "NumOfEntries: " + getNumOfEntries() + " Year: " + getYear() + " Month: " + getMonth() + " Day: " + getDay();
    }

}
