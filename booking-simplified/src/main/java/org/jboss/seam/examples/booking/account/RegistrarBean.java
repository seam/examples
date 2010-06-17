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

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.seam.examples.booking.Bundles;

import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;

/**
 * @author Dan Allen
 */
@Stateful
@RequestScoped
@Named("registrar")
public class RegistrarBean implements Registrar
{
   @PersistenceContext
   private EntityManager em;

   @Inject
   private Messages messages;

   @Inject
   private FacesContext facesContext;

   private UIInput usernameInput;

   private final User newUser = new User();

   private String confirmPassword;

   private boolean registered;

   private boolean registrationInvalid;

   public void register()
   {
      if (verifyUsernameIsAvailable())
      {
         registered = true;
         em.persist(newUser);

         messages.info(new BundleKey(Bundles.MESSAGES, "registration.registered"))
               .textDefault("You have been successfully registered as the user {0}! You can now login.")
               .textParams(newUser.getUsername());
      }
      else
      {
         registrationInvalid = true;
      }
   }

   public boolean isRegistrationInvalid()
   {
      return registrationInvalid;
   }

   /**
    * This method just shows another approach to adding a status message.
    * <p>
    * Invoked by:
    * </p>
    * 
    * <pre>
    * &lt;f:event type="preRenderView" listener="#{registrar.notifyIfRegistrationIsInvalid}"/>
    * </pre>
    */
   public void notifyIfRegistrationIsInvalid()
   {
      if (facesContext.isValidationFailed() || registrationInvalid)
      {
         messages.warn(new BundleKey(Bundles.MESSAGES, "registration.invalid"))
               .textDefault("Invalid registration. Please correct the errors and try again.");
      }
   }

   @Produces
   @RequestScoped
   @Named
   public User getNewUser()
   {
      return newUser;
   }

   public boolean isRegistered()
   {
      return registered;
   }

   public String getConfirmPassword()
   {
      return confirmPassword;
   }

   public void setConfirmPassword(final String password)
   {
      this.confirmPassword = password;
   }

   public UIInput getUsernameInput()
   {
      return usernameInput;
   }

   public void setUsernameInput(UIInput usernameInput)
   {
      this.usernameInput = usernameInput;
   }

   private boolean verifyUsernameIsAvailable()
   {
      User existing = em.find(User.class, newUser.getUsername());
      if (existing != null)
      {
         messages.warn(new BundleKey("messages", "account.usernameTaken"))
               .textDefault("The username '{0}' is already taken. Please choose another username.")
               .targets(usernameInput.getClientId())
               .textParams(newUser.getUsername());
         return false;
      }

      return true;
   }

}
