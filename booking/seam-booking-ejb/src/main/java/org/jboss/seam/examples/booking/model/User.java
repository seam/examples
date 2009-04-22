/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */
package org.jboss.seam.examples.booking.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * <p><strong>User</strong> is the model/entity class that represents a customer
 * who may book a hotel.</p>
 *
 * @author Gavin King
 * @author Dan Allen
 */
public
@Entity
@Table(name = "Customer")
class User implements Serializable
{
   private String username;
   private String password;
   private String name;

   public User(String name, String password, String username)
   {
      this.name = name;
      this.password = password;
      this.username = username;
   }

   public User()
   {
   }

   @NotNull
   @Size(max = 100)
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @NotNull
   @Size(min = 5, max = 15)
   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   @Id
   @Size(min = 4, max = 15)
   @Pattern(regexp = "^\\w*$", message = "not a valid username")
   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   @Override
   public String toString()
   {
      return "User(" + username + ")";
   }
}
