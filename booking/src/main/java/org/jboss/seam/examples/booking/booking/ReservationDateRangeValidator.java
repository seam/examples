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
package org.jboss.seam.examples.booking.booking;

import java.util.Calendar;
import java.util.Date;

import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.faces.validation.InputElement;
import org.jboss.seam.international.status.builder.BundleTemplateMessage;

/**
 * A cross-field validator that validates the start date is in the future and before the end date.
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@FacesValidator("reservationDateRange")
public class ReservationDateRangeValidator implements Validator {
    @Inject
    private InputElement<Date> startDateElement;

    @Inject
    private InputElement<Date> endDateElement;

    @Inject
    private Instance<BundleTemplateMessage> messageBuilder;

    public void validate(final FacesContext ctx, final UIComponent form, final Object value) throws ValidatorException {
        Date startDate = startDateElement.getValue();
        Date endDate = endDateElement.getValue();
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (startDate.before(calendar.getTime())) {
            String message = messageBuilder.get().key(new DefaultBundleKey("booking_checkInNotFutureDate"))
                    .targets(startDateElement.getClientId()).build().getText();
            throw new ValidatorException(new FacesMessage(message));
        } else if (!startDate.before(endDate)) {
            String message = messageBuilder.get().key(new DefaultBundleKey("booking_checkOutBeforeCheckIn"))
                    .targets(endDateElement.getClientId()).build().getText();
            throw new ValidatorException(new FacesMessage(message));
        }
    }
}
