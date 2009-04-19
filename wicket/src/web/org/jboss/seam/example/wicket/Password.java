package org.jboss.seam.example.wicket;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualInputValidator;
import org.apache.wicket.markup.html.link.PageLink;
import org.apache.wicket.markup.html.panel.ComponentFeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.example.wicket.action.ChangePassword;
import org.jboss.seam.example.wicket.action.User;

@Restrict("#{identity.loggedIn}")
public class Password extends WebPage
{
	
	@In(create=true)
	private User user;
	
	@In(create=true)
	private ChangePassword changePassword;
	
	
	public Password()
   {
	   Template body = new Template("body");
      body.add(new RegisterForm("setpassword"));
      add(body);
   }

	public class RegisterForm extends Form
	{
	   
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
         add(new PageLink("cancel", Main.class));
         FormComponent password = new PasswordTextField("password").setRequired(true);
         FormComponent verify = new PasswordTextField("verify").setRequired(true);
         add(new FormInputBorder("passwordBorder", "Password", password , new PropertyModel(user, "password")));
         add(new FormInputBorder("verifyBorder", "Verify Password", verify, new PropertyModel(this, "verify")));
         add(new ComponentFeedbackPanel("messages", this));
         add(new EqualInputValidator(password, verify));
      }
      
      @Override
      protected void onSubmit()
      {
         changePassword.changePassword();
         setResponsePage(Main.class);
      }
		
	}
	
}
