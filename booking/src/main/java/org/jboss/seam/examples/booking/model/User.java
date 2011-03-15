/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
 */
package org.jboss.seam.examples.booking.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.jboss.seam.solder.core.Veto;

/**
 * <p>
 * <strong>User</strong> is the model/entity class that represents a customer who may book a hotel.
 * </p>
 * 
 * @author Gavin King
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Entity
@Table(name = "traveler")
@Veto
public class User implements Serializable {
    private static final long serialVersionUID = -602733026033932730L;
    private String username;
    private String password;
    private String name;
    private String email;

    public User() {
    }

    public User(final String name, final String username, final String email) {
        this.name = name;
        this.username = username;
        this.email = email;
    }

    public User(final String name, final String username, final String email, final String password) {
        this(name, username, email);
        this.password = password;
    }

    @NotNull
    @Size(min = 1, max = 100)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @NotNull
    @Size(min = 5, max = 15)
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Id
    @NotNull
    @Size(min = 3, max = 15)
    @Pattern(regexp = "^\\w*$", message = "not a valid username")
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @NotNull
    @Email
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Transient
    public String getEmailWithName() {
        return name + " <" + email + ">";
    }

    @Override
    public String toString() {
        return "User(" + username + ")";
    }
}
