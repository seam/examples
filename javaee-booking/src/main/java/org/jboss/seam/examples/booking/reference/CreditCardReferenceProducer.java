/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.examples.booking.reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
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
   @Produces
   @Named
   @ConversationScoped
   public List<CreditCardType> getCreditCardTypes()
   {
      return new ArrayList<CreditCardType>(Arrays.asList(CreditCardType.values()));
   }
   
   @Produces
   @Named
   @ConversationScoped
   @CreditCardExpiryYears
   public List<Integer> getCreditCardExpiryYears()
   {
      List<Integer> years = new ArrayList<Integer>(8);
      int currentYear = Calendar.getInstance().get(Calendar.YEAR);
      for (int i = 0; i < 8; i++)
      {
         years.add(currentYear + i);
      }

      return years;
   }
}
