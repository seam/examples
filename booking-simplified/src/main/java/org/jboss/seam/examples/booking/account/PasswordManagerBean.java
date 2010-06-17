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
@Named("passwordManager")
@Stateful
@RequestScoped
public class PasswordManagerBean implements PasswordManager
{
   @PersistenceContext
   private EntityManager em;

   @Inject
   private Messages messages;

   @Inject
   @Authenticated
   private User user;

   private String confirmPassword;

   private boolean changed;

   public void changePassword()
   {
      em.merge(user);
      messages.info(new BundleKey(Bundles.MESSAGES, "account.passwordChanged"))
            .textDefault("Password successfully updated.");
      changed = true;
   }

   public boolean isChanged()
   {
      return changed;
   }

   public void setConfirmPassword(final String password)
   {
      this.confirmPassword = password;
   }

   public String getConfirmPassword()
   {
      return this.confirmPassword;
   }
}
