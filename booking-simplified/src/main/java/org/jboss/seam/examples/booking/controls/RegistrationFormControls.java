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
package org.jboss.seam.examples.booking.controls;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Default;
import javax.faces.component.UIComponent;
import javax.inject.Named;

/**
 * A UI binding bean that can provide access to the local id and client id of
 * selected input components in the registration form.
 * 
 * @author Dan Allen
 */
@Named
@RequestScoped
@Default
public class RegistrationFormControls implements Serializable
{
   private static final long serialVersionUID = -915183084620142065L;

   private UIComponent username;

   private UIComponent confirmPassword;

   public UIComponent getConfirmPassword()
   {
      return confirmPassword;
   }

   public void setConfirmPassword(final UIComponent confirmPassword)
   {
      this.confirmPassword = confirmPassword;
   }

   public String getConfirmPasswordControlId()
   {
      return confirmPassword.getClientId();
   }

   public UIComponent getUsername()
   {
      return username;
   }

   public void setUsername(final UIComponent username)
   {
      this.username = username;
   }

   public String getUsernameControlId()
   {
      return username.getClientId();
   }

}
