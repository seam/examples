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
