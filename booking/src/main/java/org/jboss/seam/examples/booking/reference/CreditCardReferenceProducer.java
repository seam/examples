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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import javax.enterprise.inject.Produces;

import org.jboss.seam.examples.booking.model.CreditCardType;

/**
 * A bean that produces credit card reference data for user-interface forms.
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class CreditCardReferenceProducer {
    @Produces
    @Named
    @ConversationScoped
    public List<CreditCardType> getCreditCardTypes() {
        return new ArrayList<CreditCardType>(Arrays.asList(CreditCardType.values()));
    }

    @Produces
    @Named
    @ConversationScoped
    @CreditCardExpiryYears
    public List<Integer> getCreditCardExpiryYears() {
        List<Integer> years = new ArrayList<Integer>(8);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < 8; i++) {
            years.add(currentYear + i);
        }

        return years;
    }
}
