/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.example.wicket;

import javax.security.auth.login.LoginException;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

public class Home extends WebPage 
{
   
   @In 
   private Identity identity;
   
   @Logger
   private Log log;

	private static final long serialVersionUID = 1L;

	public Home(final PageParameters parameters)
	{
	   add(new LoginForm("login"));
	}
	
	public class LoginForm extends Form
	{
      public LoginForm(String id)
      {
         super(id);
         add(new TextField("username", new PropertyModel(identity, "username")));
         add(new PasswordTextField("password", new PropertyModel(identity, "password")));
         add(new BookmarkablePageLink("register", Register.class));
         add(new FeedbackPanel("messages"));
      }
      
      @Override
      protected void onSubmit()
      {
         try
         {
            identity.authenticate();
            log.info("Login succeeded");
            setResponsePage(Main.class);
         }
         catch (LoginException e)
         {
            error("Login failed");
            log.error("Login failed", e);
         }
      }
	   
	}
}
