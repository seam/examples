package org.jboss.seam.examples.booking.account;

import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.builder.BundleTemplateMessage;

/**
 * @author Dan Allen
 */
@FacesValidator("currentPasswordValidator")
public class CurrentPasswordValidator implements Validator
{
   @Inject
   private Instance<BundleTemplateMessage> msg;

   @Inject
   @Authenticated
   private User currentUser;

   @SuppressWarnings("unchecked")
   public void validate(final FacesContext ctx, final UIComponent comp, final Object value) throws ValidatorException
   {
      String currentPassword = (String) value;
      if ((currentUser.getPassword() != null) && !currentUser.getPassword().equals(currentPassword))
      {
         /*
          * This is an ugly way to put i18n in FacesMessages: https://jira.jboss.org/browse/SEAMFACES-24
          */
         throw new ValidatorException(new FacesMessage(msg.get().text(
                  new DefaultBundleKey("account.passwordNotConfirmed")).build()
                  .getText()));
      }
   }

}
