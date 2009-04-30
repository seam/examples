package ui;

import javax.annotation.Named;
import javax.context.RequestScoped;
import javax.faces.component.UIComponent;

/**
 * A UI binding bean that can provide access to the local id and client id
 * of selected input components in the registration form.
 *
 * @author Dan Allen
 */
public
@Named
@RequestScoped
class RegistrationFormControls
{
   private UIComponent username;

   private UIComponent confirmPassword;

   public UIComponent getConfirmPassword()
   {
      return confirmPassword;
   }

   public void setConfirmPassword(UIComponent confirmPassword)
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

   public void setUsername(UIComponent username)
   {
      this.username = username;
   }

   public String getUsernameControlId()
   {
      return username.getClientId();
   }

}
