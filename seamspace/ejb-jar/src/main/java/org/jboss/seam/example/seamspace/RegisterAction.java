package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.event.Observes;
import javax.inject.Current;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.Member;
import org.jboss.seam.example.seamspace.model.MemberAccount;
import org.jboss.seam.example.seamspace.model.MemberImage;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.RunAsOperation;
import org.jboss.seam.security.events.UserCreatedEvent;
import org.jboss.seam.security.management.IdentityManager;

@Named("register")
@ConversationScoped
public class RegisterAction implements Serializable
{
   private static final long serialVersionUID = 2943705513616351130L;

   private Member member;
   
   @PersistenceContext EntityManager entityManager;
   
   @Current Identity identity;
   @Current Credentials credentials;
   
   @Current IdentityManager identityManager;
   
   @Current Conversation conversation;
   @Current StatusMessages messages;
      
   private MemberAccount newAccount;
   
   private String username;   
   
   /**
    * Password confirmation
    */
   private String password;
   private String confirm;   
   
   private String gender;
   
   private byte[] picture;
   private String pictureContentType;
   
   private boolean verified;

   public void start()
   {
      conversation.begin();
      member = new Member();
   }
   
   public void next()
   {
      member.setGender(Member.Gender.valueOf(gender.toLowerCase()));
      
      verified = (confirm != null && confirm.equals(password));
      
      if (!verified)
      {
         messages.addToControl("confirmPassword", "Passwords do not match");
      }           
   }
   
   public void accountCreated(@Observes UserCreatedEvent event)
   {
      MemberAccount account = (MemberAccount) event.getUser();
      
      // The user *may* have been created from the user manager screen. In that
      // case, create a dummy Member record just for the purpose of demonstrating the
      // identity management API
      if (member == null)
      {
         member = new Member();
         member.setMemberName(account.getUsername());
         member.setGender(Member.Gender.male);
         member.setFirstName("John");
         member.setLastName("Doe");
         member.setEmail(account.getUsername() + "@nowhere.com");
         member.setDob(new Date());
         member.setMemberSince(new Date());
         entityManager.persist(member);
      }
      
      account.setMember(member);
      this.newAccount = account;
   }

   public void uploadPicture() 
   {  
      member.setMemberSince(new Date());      
      entityManager.persist(member);      
      
      identity.runAs(
        new RunAsOperation() {
         public void execute() {
            identityManager.createUser(username, password);
            identityManager.grantRole(username, "user");            
         }         
      }.addRole("admin"));
            
      newAccount.setMember(member);
      newAccount = entityManager.merge(newAccount);

      if (picture != null && picture.length > 0)
      {
         MemberImage img = new MemberImage();
         img.setData(picture);
         img.setMember(member);
         img.setContentType(pictureContentType);
         entityManager.persist(img);
         member.setPicture(img);
         
         member = entityManager.merge(member);
      }
      
      // Login the user
      credentials.setUsername(username);
      credentials.setPassword(password);
      identity.login();
      
      conversation.end();
   }
   
   public Member getMember()
   {
      return member;
   }
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
   }
   
   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      this.password = password;
   }
   
   public String getConfirm()
   {
      return confirm;
   }
   
   public void setConfirm(String confirm)
   {
      this.confirm = confirm;
   }
   
   public String getGender()
   {
      return gender;
   }
   
   public void setGender(String gender)
   {
      this.gender = gender;
   }
   
   public void setPicture(byte[] picture)
   {
      this.picture = picture;
   }
   
   public byte[] getPicture()
   {
      return picture;
   }
   
   public String getPictureContentType()
   {
      return pictureContentType;  
   }
   
   public void setPictureContentType(String contentType)
   {
      this.pictureContentType = contentType;
   }
   
   public boolean isVerified()
   {
      return verified;
   }
}
