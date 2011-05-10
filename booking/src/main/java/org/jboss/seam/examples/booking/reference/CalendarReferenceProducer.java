/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.examples.booking.reference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * Produces calendar-oriented reference data to be used in user-interface forms. The user's locale is honored when producing
 * name-based data.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class CalendarReferenceProducer {

    @Produces
    @Named
    @ConversationScoped
    public List<Month> getMonths(Locale locale) {
        List<Month> months = new ArrayList<Month>(12);
        DateFormat longNameFormat = new SimpleDateFormat("MMMM", locale);
        DateFormat shortNameFormat = new SimpleDateFormat("MMM", locale);
        Calendar cal = Calendar.getInstance();
        for (int m = 0; m < 12; m++) {
            cal.set(Calendar.MONTH, m);
            months.add(new Month(m, longNameFormat.format(cal.getTime()), shortNameFormat.format(cal.getTime())));
        }

        return months;
    }

}
