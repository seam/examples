package org.jboss.seam.examples.booking.reference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import javax.annotation.Named;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.inject.Produces;

/**
 * Produces calendar-oriented reference data to be used in user-interface forms.
 * The user's locale is honored when producing name-based data.
 *
 * @author Dan Allen
 */
public class CalendarReferenceProducer {

   @Current Locale locale;

   public
   @Produces
   @Named
   @ConversationScoped
   List<Month> getMonths()
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

   public
   @Produces
   @Named
   @ConversationScoped
   @CreditCardExpiryYears
   List<Integer> getCreditCardExpiryYears()
   {
      List<Integer> years = new ArrayList<Integer>(8);
      int currentYear = Calendar.getInstance().get(Calendar.YEAR);
      for (int y = currentYear; y <= (currentYear + 8); y++)
      {
         years.add(y);
      }

      return years;
   }
}
