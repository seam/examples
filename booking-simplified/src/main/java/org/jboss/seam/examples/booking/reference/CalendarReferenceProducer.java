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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.enterprise.inject.Produces;

/**
 * Produces calendar-oriented reference data to be used in user-interface forms.
 * The user's locale is honored when producing name-based data.
 *
 * @author Dan Allen
 */
public class CalendarReferenceProducer {

   @Produces
   @Named
   @ConversationScoped
   public List<Month> getMonths(Locale locale)
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
