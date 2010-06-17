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
package org.jboss.seam.examples.booking.booking;

import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import org.jboss.seam.examples.booking.Bundles;
import org.jboss.seam.faces.validation.InputField;
import org.jboss.seam.international.status.MessageFactory;
import org.jboss.seam.international.status.builder.BundleKey;

/**
 * A cross-field validator that validates the begin date
 * is in the future and before the end date.
 *
 * @author Dan Allen
 */
@FacesValidator("reservationDateRangeValidator")
public class ReservationDateRangeValidator implements Validator {
   @Inject
   @InputField
   private Date beginDate;

   @Inject
   @InputField
   private Date endDate;

   @Inject
   private MessageFactory msg;

   @Override
   public void validate(FacesContext ctx, UIComponent c, Object value) throws ValidatorException
   {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      if (beginDate.before(calendar.getTime()))
      {
         String message = msg.info(new BundleKey(Bundles.MESSAGES, "booking.checkInNotFutureDate"))
               .textDefault("Check-in date must be in the future")
               // FIXME this information should come through via injection
               .targets("checkInDate:input")
               .build().getText();
         throw new ValidatorException(new FacesMessage(message));
      }
      else if (!beginDate.before(endDate))
      {
         String message = msg.info(new BundleKey(Bundles.MESSAGES, "booking.checkOutBeforeCheckIn"))
               .textDefault("Check-out date must be after check-in date")
               // FIXME this information should come through via injection
               .targets("checkOutDate:input")
               .build().getText();
         throw new ValidatorException(new FacesMessage(message));
      }
   }

}
