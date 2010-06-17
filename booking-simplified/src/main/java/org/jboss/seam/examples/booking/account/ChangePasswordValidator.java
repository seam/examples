package org.jboss.seam.examples.booking.account;

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
import org.jboss.seam.examples.booking.Bundles;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.faces.validation.InputField;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.seam.international.status.builder.BundleTemplateMessage;

/**
 * @author Dan Allen
 */
@FacesValidator(value = "changePasswordValidator")
public class ChangePasswordValidator implements Validator
      // extending throws an unsatisified dependency exception
      //extends ConfirmPasswordValidator
{
   @Inject
   private Instance<BundleTemplateMessage> messageBuilder;

   @Inject
   @Authenticated
   private User currentUser;

   @Inject
   @InputField
   private String currentPassword;

   @Override
   public void validate(FacesContext ctx, UIComponent form, Object value) throws ValidatorException
   {
      Map<String, UIInput> fieldMap = (Map<String, UIInput>) value;
      if (currentUser.getPassword() != null && !currentUser.getPassword().equals(currentPassword))
      {
         /*
          * This is an ugly way to put i18n in FacesMessages:
          * https://jira.jboss.org/browse/SEAMFACES-24
          */
         throw new ValidatorException(
               new FacesMessage(messageBuilder.get().text(
                     new BundleKey(Bundles.MESSAGES, "account.passwordNotConfirmed"))
                           .targets(fieldMap.get("oldPassword").getClientId())
                           .build().getText()));
      }

      // TODO enable when we can extend
      //super.validate(ctx, form, value);
   }

}
