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
import javax.enterprise.inject.Model;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;

/**
 * The view controller for changing the user password
 * 
 * @author Dan Allen
 */
@Stateful @Model
public class PasswordManager
{
	@PersistenceContext
	private EntityManager em;

	@Inject
	private Messages messages;

	@Inject @Authenticated
	private User user;

	@NotNull @Size(min = 5, max = 15)
	private String confirmPassword;
	
	@NotNull @Size(min = 5, max = 15)
	private String newPassword;

	private boolean changed;

	public void changePassword()
	{
		user.setPassword(newPassword);
		em.merge(user);
		messages.info(new DefaultBundleKey("account_passwordChanged")).defaults("Password successfully updated.");
		changed = true;
	}

	public boolean isChanged()
	{
		return changed;
	}

	public void setConfirmPassword(final String password)
	{
		confirmPassword = password;
	}

	public String getConfirmPassword()
	{
		return confirmPassword;
	}
	

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

}
