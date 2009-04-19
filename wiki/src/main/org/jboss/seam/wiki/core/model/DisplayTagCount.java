package org.jboss.seam.wiki.core.model;

import java.io.Serializable;

/**
 * A simple DTO.
 *
 * @author Christian Bauer
 */
public class DisplayTagCount implements Comparable, Serializable {
    String tag;
    Long count;

    public DisplayTagCount() {}

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public int compareTo(Object o) {
        int result = ((DisplayTagCount)o).getCount().compareTo( this.getCount() );
        return result == 0
            ? this.getTag().compareTo( ((DisplayTagCount)o).getTag() )
            : result;
    }

    public String toString() {
        return "TagCount(" + getCount() + "): " + getTag();
    }
}
