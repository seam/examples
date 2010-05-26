package org.jboss.seam.examples.booking.reference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.enterprise.inject.Produces;

/**
 * Produces calendar-oriented reference data to be used in user-interface forms.
 * The user's locale is honored when producing name-based data.
 *
 * @author Dan Allen
 */
public class CalendarReferenceProducer {

   @Inject private Locale locale;

   @Produces
   @Named
   @ConversationScoped
   public List<Month> getMonths()
   {
      List<Month> months = new ArrayList<Month>(12);
      DateFormat longNameFormat = new SimpleDateFormat("MMMM", locale);
      DateFormat shortNameFormat = new SimpleDateFormat("MMM", locale);
      Calendar cal = Calendar.getInstance();
      for (int m = 0; m < 12; m++)
      {
         cal.set(Calendar.MONTH, m);
         months.add(new Month(m, longNameFormat.format(cal.getTime()), shortNameFormat.format(cal.getTime())));
      }

      return months;
   }

}
