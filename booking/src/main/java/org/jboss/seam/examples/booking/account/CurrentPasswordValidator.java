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
package org.jboss.seam.examples.booking.account;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.jboss.seam.examples.booking.i18n.DefaultBundleKey;
import org.jboss.seam.examples.booking.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleTemplateMessage;

/**
 * A JSF Validator, used to check that the password the user submits matches that on record.
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@FacesValidator("currentPassword")
public class CurrentPasswordValidator implements Validator {
    @Inject
    private BundleTemplateMessage messageBuilder;

    @Inject
    @Authenticated
    private User currentUser;

    @Inject
    Messages messages;

    public void validate(final FacesContext ctx, final UIComponent comp, final Object value) throws ValidatorException {
        String currentPassword = (String) value;
        if ((currentUser.getPassword() != null) && !currentUser.getPassword().equals(currentPassword)) {
            /*
             * FIXME: This is an ugly way to put i18n in FacesMessages: https://jira.jboss.org/browse/SEAMFACES-24
             */

            throw new ValidatorException(new FacesMessage(messageBuilder
                    .key(new DefaultBundleKey("account_passwordsDoNotMatch")).defaults("Passwords do not match").build()
                    .getText()));
        }
    }

}
