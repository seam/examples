package org.jboss.seam.international.examples.timeanddate.model;

import javax.inject.Named;

/**
 * Enum for regions
 *
 * @author <a href="http://community.jboss.org/people/spinner)">jose.freitas</a>
 */
@Named
public enum TimeZoneRegions {
    AFRICA("Africa"),
    AMERICA("America"),
    ASIA("Asia"),
    AUSTRALIA("Australia"),
    ATLANTIC("Atlantic"),
    EUROPE("Europe"),
    INDIAN("Indian"),
    PACIFIC("Pacific");

    String region;

    TimeZoneRegions(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }


}
