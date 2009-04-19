package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.security.management.PasswordSalt;
import org.jboss.seam.annotations.security.management.UserEnabled;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class MemberAccount implements Serializable
{
   private static final long serialVersionUID = 6368734442192368866L;
   
   private Integer accountId;
   private String username;
   private String passwordHash;
   private String passwordSalt;
   private boolean enabled;   
   
   private Set<MemberRole> roles;
   private Member member;   
   
   @Id @GeneratedValue
   public Integer getAccountId()
   {
      return accountId;
   }
   
   public void setAccountId(Integer accountId)
   {
      this.accountId = accountId;
   }
   
   @NotNull @UserPrincipal
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
   }
   
   @UserPassword
   public String getPasswordHash()
   {
      return passwordHash;
   }
   
   public void setPasswordHash(String passwordHash)
   {
      this.passwordHash = passwordHash;      
   }
   
   @PasswordSalt
   public String getPasswordSalt()
   {
      return passwordSalt;
   }
   
   public void setPasswordSalt(String passwordSalt)
   {
      this.passwordSalt = passwordSalt;
   }
   
   @UserEnabled
   public boolean isEnabled()
   {
      return enabled;
   }

   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;      
   }   

   @UserRoles
   @ManyToMany(targetEntity = MemberRole.class)
   @JoinTable(name = "AccountMembership", 
         joinColumns = @JoinColumn(name = "AccountId"),
         inverseJoinColumns = @JoinColumn(name = "MemberOf")
      )
   public Set<MemberRole> getRoles()
   {
      return roles;
   }
   
   public void setRoles(Set<MemberRole> roles)
   {
      this.roles = roles;
   }
   
   @OneToOne
   @JoinColumn(name = "MEMBER_ID")
   public Member getMember()
   {
      return member;
   }
   
   public void setMember(Member member)
   {
      this.member = member;
   }
}
