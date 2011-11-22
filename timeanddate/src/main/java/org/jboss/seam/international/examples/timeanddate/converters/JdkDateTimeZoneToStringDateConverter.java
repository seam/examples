package org.jboss.seam.international.examples.timeanddate.converters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.international.examples.timeanddate.worldclock.AvailableTimeZonesBean;

/**
 * Converter used to convert a java.util.TimeZone to a formatted date
 *
 * @author <a href="http://community.jboss.org/people/spinner)">jose.freitas</a>
 */
public class JdkDateTimeZoneToStringDateConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesComponent, UIComponent uiComponent, String value) {
        // not going to be used for now
        return null;
    }

    @Override
    public String getAsString(FacesContext facesComponent, UIComponent uiComponent, Object object) {
        Date now = Calendar.getInstance((TimeZone) object).getTime();
        DateFormat dfm = new SimpleDateFormat(AvailableTimeZonesBean.STANDARD_DATE_TIME_FORMAT);

        return dfm.format(now);
    }

}
