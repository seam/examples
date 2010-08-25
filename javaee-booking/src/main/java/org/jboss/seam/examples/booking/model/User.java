/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.seam.examples.booking.model;

import java.io.Serializable;

import javax.enterprise.inject.Typed;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.jboss.weld.extensions.core.Veto;

/**
 * <p>
 * <strong>User</strong> is the model/entity class that represents a customer
 * who may book a hotel.
 * </p>
 * 
 * @author Gavin King
 * @author Dan Allen
 */
@Entity
@Table(name = "traveler")
@Veto
public class User implements Serializable
{
   private String username;
   private String password;
   private String name;
   private String email;

   public User()
   {
   }

   public User(final String name, final String username, final String email)
   {
      this.name = name;
      this.username = username;
      this.email = email;
   }

   public User(final String name, final String username, final String email, final String password)
   {
      this(name, username, email);
      this.password = password;
   }

   @NotNull
   @Size(min = 1, max = 100)
   public String getName()
   {
      return name;
   }

   public void setName(final String name)
   {
      this.name = name;
   }

   @NotNull
   @Size(min = 5, max = 15)
   public String getPassword()
   {
      return password;
   }

   public void setPassword(final String password)
   {
      this.password = password;
   }

   @Id
   @NotNull
   @Size(min = 3, max = 15)
   @Pattern(regexp = "^\\w*$", message = "not a valid username")
   public String getUsername()
   {
      return username;
   }

   public void setUsername(final String username)
   {
      this.username = username;
   }

   @NotNull
   @Email
   public String getEmail()
   {
      return email;
   }

   public void setEmail(final String email)
   {
      this.email = email;
   }

   @Transient
   public String getEmailWithName()
   {
      return name + " <" + email + ">";
   }

   @Override
   public String toString()
   {
      return "User(" + username + ")";
   }
}
