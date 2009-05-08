package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Named;
import javax.context.Conversation;
import javax.context.ConversationScoped;
import javax.inject.Current;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.example.seamspace.model.Member;
import org.jboss.seam.example.seamspace.model.MemberAccount;
import org.jboss.seam.example.seamspace.model.MemberImage;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.SimplePrincipal;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.permission.Permission;
import org.jboss.seam.security.permission.PermissionManager;
import org.jboss.seam.security.permission.action.PermissionSearch;

@Named
@ConversationScoped
public class ImagePermission implements Serializable
{
   private static final long serialVersionUID = -4943654157860780587L;

   private List<String> selectedRoles;   
   private List<Member> selectedFriends;
   private List<String> selectedActions;
   
   private List<String> originalActions;
   
   private List<Member> availableFriends;   
   
   @Current IdentityManager identityManager;
   @Current PermissionManager permissionManager;
   
   @PersistenceContext EntityManager entityManager;
   
   @Current PermissionSearch permissionSearch;
   @Current Conversation conversation;
   @Current StatusMessages messages;
   
   private MemberImage target; 
   
   private Principal recipient;
   
   @SuppressWarnings("unchecked")
   public void createPermission()
   {
      conversation.begin();
      target = (MemberImage) permissionSearch.getTarget();
      
      selectedFriends = new ArrayList<Member>();
      
      availableFriends = entityManager.createQuery(
            "select f.friend from MemberFriend f where f.member = :member and f.authorized = true")
            .setParameter("member", target.getMember())
            .getResultList();      
   }
   
   public void editPermission()
   {
      conversation.begin();
      target = (MemberImage) permissionSearch.getTarget();
      recipient = permissionSearch.getSelectedRecipient();
            
      List<Permission> permissions = permissionManager.listPermissions(target);
      
      selectedActions = new ArrayList<String>();      
      
      for (Permission permission : permissions)
      {
         if (permission.getRecipient().equals(recipient))
         {
            if (!selectedActions.contains(permission.getAction()))
            {
               selectedActions.add(permission.getAction());
            }
         }
      }
      
      originalActions = new ArrayList<String>(selectedActions);
   }

   public List<String> getSelectedRoles()
   {
      return selectedRoles;
   }
   
   public void setSelectedRoles(List<String> selectedRoles)
   {
      this.selectedRoles = selectedRoles;
   }
   
   public List<Member> getSelectedFriends()
   {
      return selectedFriends;
   }
   
   public void setSelectedFriends(List<Member> selectedFriends)
   {
      this.selectedFriends = selectedFriends;
   }
   
   public List<String> getSelectedActions()
   {
      return selectedActions;
   }
   
   public void setSelectedActions(List<String> selectedActions)
   {
      this.selectedActions = selectedActions;
   }
   
   public String applyPermissions()
   {
      // If the recipient isn't null, it means we're editing existing permissions
      if (recipient != null)
      {
         List<Permission> grantedPermissions = new ArrayList<Permission>();
         List<Permission> revokedPermissions = new ArrayList<Permission>();
         
         for (String action : selectedActions)
         {
            if (!originalActions.contains(action)) 
            {
               grantedPermissions.add(new Permission(target, action, recipient));
            }
         }
         
         for (String action : originalActions)
         {
            if (!selectedActions.contains(action))
            {
               revokedPermissions.add(new Permission(target, action, recipient));
            }
         }
         
         if (!grantedPermissions.isEmpty()) permissionManager.grantPermissions(grantedPermissions);
         if (!revokedPermissions.isEmpty()) permissionManager.revokePermissions(revokedPermissions);
      }
      // otherwise this is a set of new permissions
      else
      {
         if (selectedActions.size() == 0)
         {
            messages.add("You must select at least one action");
            return "failure";
         }
         
         List<Permission> permissions = new ArrayList<Permission>();
   
         for (String role : selectedRoles)
         {
            Principal r = new Role(role);
            for (String action : selectedActions)
            {            
               permissions.add(new Permission(target, action, r));
            }
         }
         
         for (Member friend : selectedFriends)
         {
            MemberAccount acct = (MemberAccount) entityManager.createQuery(
                  "select a from MemberAccount a where a.member = :member")
                  .setParameter("member", friend)
                  .getSingleResult();
            
            Principal p = new SimplePrincipal(acct.getUsername());
            
            for (String action : selectedActions)
            {
               permissions.add(new Permission(target, action, p));
            }
         }
         
         permissionManager.grantPermissions(permissions);
      }
      conversation.end();
      return "success";
   }
   
   public List<Member> getAvailableFriends()
   {
      return availableFriends;
   }
   
   public MemberImage getTarget()
   {
      return target;
   }
   
   public Principal getRecipient()
   {
      return recipient;
   }
}
