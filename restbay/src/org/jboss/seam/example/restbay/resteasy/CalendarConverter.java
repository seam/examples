package org.jboss.seam.example.restbay.resteasy;

import org.jboss.resteasy.spi.StringConverter;

import javax.ws.rs.ext.Provider;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Converts QueryParam etc. strings to GregorianCalendar if they are in ISO date format
 * @author Christian Bauer
 */
@Provider
public class CalendarConverter implements StringConverter<GregorianCalendar>
{
   public static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

   public GregorianCalendar fromString(String s)
   {
      SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
      GregorianCalendar cal = new GregorianCalendar();
      try
      {
         cal.setTime(sdf.parse(s));
      }
      catch (ParseException e)
      {
         throw new RuntimeException(e);
      }
      return cal;

   }

   // TODO: RESTEasy doesn't seem to use that at all
   public String toString(GregorianCalendar gregorianCalendar)
   {
      SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT);
      return sdf.format(gregorianCalendar.getTime());
   }
}
