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
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.faces.validation.InputField;
import org.jboss.seam.international.status.builder.BundleTemplateMessage;

/**
 * A cross-field validator that validates the start date is in the future and
 * before the end date.
 * 
 * @author Dan Allen
 */
@FacesValidator("reservationDateRange")
public class ReservationDateRangeValidator implements Validator
{
	@Inject
	@InputField
	private Date startDate;

	@Inject
	@InputField
	private Date endDate;

	@Inject
	private Instance<BundleTemplateMessage> messageBuilder;


	public void validate(final FacesContext ctx, final UIComponent form, final Object value) throws ValidatorException
	{
		@SuppressWarnings("unchecked")
		Map<String, UIInput> fieldMap = (Map<String, UIInput>) value;

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		if (startDate.before(calendar.getTime()))
		{
			String message = messageBuilder.get().text(new DefaultBundleKey("booking_checkInNotFutureDate"))
			// FIXME the component should come through via injection
			.targets(fieldMap.get("beginDate").getClientId()).build().getText();
			throw new ValidatorException(new FacesMessage(message));
		}
		else if (!startDate.before(endDate))
		{
			String message = messageBuilder.get().text(new DefaultBundleKey("booking_checkOutBeforeCheckIn"))
			// FIXME the component should come through via injection
			.targets(fieldMap.get("endDate").getClientId()).build().getText();
			throw new ValidatorException(new FacesMessage(message));
		}
	}
}
