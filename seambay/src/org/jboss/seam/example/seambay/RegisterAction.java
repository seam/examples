package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;

@Scope(CONVERSATION)
@Name("registerAction")
public class RegisterAction implements Serializable
{
   private static final long serialVersionUID = -4349512217411197622L;
   
   @In
   EntityManager entityManager;

   @Out
   private User newuser;
   
   private String confirm;
   
   @Begin(join = true)
   public void newRegistration()
   {
      if (newuser == null)
      {
         newuser = new User();
         newuser.setAccount(new Account());
      }
   }
   
   @End(ifOutcome = "success")
   public String register()
   {
      if (confirm == null || !confirm.equals(newuser.getPassword()))
      {
         FacesMessages.instance().addToControl("confirm", "Passwords do not match");
         return "failed";
      }
      
      if (entityManager.createQuery("from User where username = :username")
            .setParameter("username", newuser.getUsername())
            .getResultList().size() > 0)
      {
         FacesMessages.instance().addToControl("username", 
               "That user ID is already taken, please choose a different one");
         return "failed"; 
      }
      
      newuser.getAccount().setFeedbackPercent(0);
      newuser.getAccount().setFeedbackScore(0);
      newuser.getAccount().setMemberSince(new Date());
      
      // The account name *could* be different to the username if we want
      newuser.getAccount().setName(newuser.getUsername());
      
      try
      {
         entityManager.persist(newuser.getAccount());
         entityManager.persist(newuser);
         
         Identity.instance().setUsername(newuser.getUsername());
         Identity.instance().setPassword(newuser.getPassword());
         Identity.instance().login();
         
         return "success";
      }
      catch (EntityExistsException ex)
      {
         FacesMessages.instance().addToControl("username", 
               "That user ID is already taken, please choose a different one");
         return "failed";  
      }
   }
   
   public String getConfirm()
   {
      return confirm;
   }
   
   public void setConfirm(String confirm)
   {
      this.confirm = confirm;
   }

}
