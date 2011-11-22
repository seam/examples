package org.jboss.seam.international.examples.timeanddate.worldclock;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * AvailableTimeZonesBean is the bean responsable to keep and produce a list of dateTimes extracted from
 * the available TimeZones produced by seam-international.
 *
 * @author <a href="http://community.jboss.org/people/spinner)">jose.freitas</a>
 */
@Singleton
@Startup
public class AvailableTimeZonesBean {

    public final static String STANDARD_DATE_TIME_FORMAT = "HH:mm:ss MM/dd/yyyy";

    @Inject
    List<DateTimeZone> timeZones;

    @Inject
    DateTimeZone applicationTimeZone;

    @Inject
    private Logger log;


    @PostConstruct
    public void startupTimeAroundTheWorld() {
        log.info("This bean started on: ");
        for (Iterator<DateTimeZone> iterator = timeZones.iterator(); iterator.hasNext();) {
            DateTimeZone dtz = iterator.next();
            DateTimeFormatter fmt = DateTimeFormat.forPattern(STANDARD_DATE_TIME_FORMAT);
            DateTime dt = new DateTime().withZone(dtz);

            log.info(dtz.getID() + " - " + fmt.print(dt));
        }
    }

    @Produces
    @Named("availableDateTimesFromTimeZones")
    public List<DateTime> getAvailableTimeZones() {
        List<DateTimeZone> filteredTimeZones = filterTimeZones();

        List<DateTime> list = new LinkedList<DateTime>();
        for (Iterator<DateTimeZone> iterator = filteredTimeZones.iterator(); iterator.hasNext();) {
            DateTimeZone dtz = iterator.next();
            DateTime dateTime = new DateTime().withZone(dtz);
            list.add(dateTime);
        }
        return list;
    }

    private List<DateTimeZone> filterTimeZones() {
        String region = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("region");
        if (region != null) {
            List<DateTimeZone> filteredTimeZones = new LinkedList<DateTimeZone>();
            for (Iterator<DateTimeZone> iterator = timeZones.iterator(); iterator.hasNext();) {
                DateTimeZone dtz = iterator.next();
                if (dtz.getID().startsWith(region))
                    filteredTimeZones.add(dtz);
            }
            return filteredTimeZones;
        }
        return timeZones;
    }
}
