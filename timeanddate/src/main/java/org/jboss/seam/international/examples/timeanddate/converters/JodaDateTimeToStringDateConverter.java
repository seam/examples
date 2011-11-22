package org.jboss.seam.international.examples.timeanddate.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.international.examples.timeanddate.worldclock.AvailableTimeZonesBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Converter used to convert a Joda DateTime to a formatted date
 *
 * @author <a href="http://community.jboss.org/people/spinner)">jose.freitas</a>
 */
public class JodaDateTimeToStringDateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesComponent, UIComponent uiComponent, String value) {
        // not going to be used for now.
        return null;
    }

    @Override
    public String getAsString(FacesContext facesComponent, UIComponent uiComponent, Object object) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(AvailableTimeZonesBean.STANDARD_DATE_TIME_FORMAT);
        DateTime dateTime = (DateTime) object;

        return fmt.print(dateTime);
    }

}
