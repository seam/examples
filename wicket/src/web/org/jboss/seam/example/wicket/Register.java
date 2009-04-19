package org.jboss.seam.example.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.example.wicket.action.User;
import org.jboss.seam.wicket.SeamPropertyModel;

public class Register extends WebPage
{
	
	@In(create=true)
	private User user;
	
	@In(create=true)
	private org.jboss.seam.example.wicket.action.Register register;
	
	private TextField username;
	
	
	public Register()
    {
	    Form registerForm = new RegisterForm("registration");  
       add(registerForm);
       registerForm.add(new ComponentFeedbackPanel("messages", this));
    }

	public class RegisterForm extends Form
	{

	   @Out(scope=ScopeType.EVENT, required=false)
	   private String verify;
	   
	   public String getVerify()
      {
         return verify;
      }
	   
	   public void setVerify(String verify)
      {
         this.verify = verify;
      }
	   
      public RegisterForm(String id)
      {
         super(id);
         add(new PageLink("cancel", Home.class));
         username = new TextField("username");
         username.setRequired(true);
         add(new FormInputBorder("usernameDecorate", "Username", username, new SeamPropertyModel("username")
         {
            
            @Override
            public Object getTarget()
            {
               return user;
            }
            
         }));
         add(new FormInputBorder("nameDecorate", "Real Name", new TextField("name").setRequired(true), new SeamPropertyModel("name")
         {
            @Override
            public Object getTarget()
            {
               return user;
            }
         }));
         FormComponent password = new PasswordTextField("password").setRequired(true);
         FormComponent verify = new PasswordTextField("verify").setRequired(true);
         add(new FormInputBorder("passwordDecorate", "Password", password , new SeamPropertyModel("password")
         {
            @Override
            public Object getTarget()
            {
               return user;
            }
         }));
         add(new FormInputBorder("verifyDecorate", "Verify Password", verify, new PropertyModel(this, "verify")));
         add(new EqualPasswordInputValidator(password, verify));
      }
      
      @Override
      protected void onSubmit()
      {
         register.register();
         if (register.isRegistered())
         {
            setResponsePage(Home.class);
         }
      }
      
	}
	
}
