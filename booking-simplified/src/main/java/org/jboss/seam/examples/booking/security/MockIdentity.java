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
package org.jboss.seam.examples.booking.security;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Named("identity")
@SessionScoped
public class MockIdentity implements Serializable
{
   private boolean loggedIn;

   @Inject
   FacesContext context;

   @Inject
   Messages messages;

   @Inject
   MockCredentials credentials;

   public boolean isLoggedIn()
   {
      return loggedIn;
   }

   public void setLoggedIn(final boolean loggedIn)
   {
      this.loggedIn = loggedIn;
   }

   public void login()
   {
      if ((credentials.getUsername() != null) && !"".equals(credentials.getUsername().trim()))
      {
         loggedIn = true;
         messages.info(new BundleKey("messages.properties", "identity.loggedIn"));
      }
      else
      {
         messages.info(new BundleKey("messages.properties", "identity.loginFailed"));
      }
   }

   public void logout()
   {
      loggedIn = false;
      HttpSession session = (HttpSession) context.getExternalContext().getSession(true);
      session.invalidate();
   }

}
