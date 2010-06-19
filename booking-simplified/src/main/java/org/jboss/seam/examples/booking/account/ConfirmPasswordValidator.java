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
package org.jboss.seam.examples.booking.account;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.faces.validation.InputField;
import org.jboss.seam.international.status.builder.BundleTemplateMessage;

/**
 * Validate that both the password fields contain the same value. Implements the
 * classic pasword change validation.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacesValidator("confirmPassword")
public class ConfirmPasswordValidator implements Validator
{
   @Inject
   private BundleTemplateMessage messageBuilder;

   @Inject @InputField
   private String newPassword;

   @Inject @InputField
   private String confirmPassword;

   public void validate(final FacesContext ctx, final UIComponent form, final Object components) throws ValidatorException
   {
      if ((newPassword != null) && !newPassword.equals(confirmPassword))
      {
         throw new ValidatorException(new FacesMessage(messageBuilder.text(new DefaultBundleKey("account_passwordsDoNotMatch")).build().getText()));
      }
   }

}
