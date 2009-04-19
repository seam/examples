package org.jboss.seam.example.restbay.resteasy;

import javax.ws.rs.GET;
import java.util.GregorianCalendar;

/**
 * @author Christian Bauer
 */
public class CalendarResource
{
   private GregorianCalendar cal;

   public CalendarResource(GregorianCalendar cal)
   {
      this.cal = cal;
   }

   @GET
   public long get()
   {
      System.out.println("#### GET");
      return cal.getTime().getTime();
   }

}
