package org.jboss.seam.examples.booking.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Named;
import javax.enterprise.inject.Produces;

import org.jboss.seam.examples.booking.model.CreditCardType;

/**
 * A bean that produces credit card reference data for
 * user-interface forms.
 * 
 * @author Dan Allen
 */
public class CreditCardReferenceProducer
{
   public
   @Produces
   @Named
   @ConversationScoped
   List<CreditCardType> getCreditCardTypes()
   {
      return new ArrayList<CreditCardType>(Arrays.asList(CreditCardType.values()));
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
