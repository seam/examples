package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.EVENT;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("memberAction")
@Scope(EVENT)
public class MemberAction implements Serializable
{
   private static final long serialVersionUID = -8233305696689620298L;

   private String memberName;   
   
   private Account selectedMember;
   
   @In 
   EntityManager entityManager;   
   
   @Factory(value="selectedMember", scope=ScopeType.PAGE)
   public Account getSelectedMember()
   {
      if (selectedMember == null && memberName != null)
      {
         selectedMember = (Account) entityManager.createQuery(
            "from Account where name = :name")
            .setParameter("name", memberName)
            .getSingleResult();             
      }
      
      return selectedMember;
   }    
   
   public String getMemberName()
   {
      return memberName;
   }
   
   public void setMemberName(String memberName)
   {
      this.memberName = memberName;
   }   
}
