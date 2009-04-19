package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.jboss.seam.annotations.security.management.RoleConditional;
import org.jboss.seam.annotations.security.management.RoleGroups;
import org.jboss.seam.annotations.security.management.RoleName;

@Entity
public class MemberRole implements Serializable
{
   private static final long serialVersionUID = 9177366120789064801L;
   
   private Integer roleId;
   private String name;
   private boolean conditional;
   
   private Set<MemberRole> groups;
   
   @Id @GeneratedValue
   public Integer getRoleId()
   {
      return roleId;
   }
   
   public void setRoleId(Integer roleId)
   {
      this.roleId = roleId;
   }
   
   @RoleName
   public String getName()
   {
      return name;
   }
   
   public void setName(String name)
   {
      this.name = name;
   }
   
   @RoleGroups
   @ManyToMany(targetEntity = MemberRole.class)
   @JoinTable(name = "RoleGroup", 
         joinColumns = @JoinColumn(name = "RoleId"),
         inverseJoinColumns = @JoinColumn(name = "MemberOf")
      )
   public Set<MemberRole> getGroups()
   {
      return groups;
   }
   
   public void setGroups(Set<MemberRole> groups)
   {
      this.groups = groups;
   }   
   
   @RoleConditional
   public boolean isConditional()
   {
      return conditional;
   }
   
   public void setConditional(boolean conditional)
   {
      this.conditional = conditional;
   }
}
