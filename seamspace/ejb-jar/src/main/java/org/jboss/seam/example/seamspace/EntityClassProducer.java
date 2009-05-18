package org.jboss.seam.example.seamspace;

import org.jboss.seam.example.seamspace.model.MemberAccount;
import org.jboss.seam.example.seamspace.model.MemberRole;
import org.jboss.seam.security.management.IdentityStoreEntityClasses;

public class EntityClassProducer implements IdentityStoreEntityClasses
{
   public Class<?> getRoleEntityClass()
   {
      return MemberRole.class;
   }
   public Class<?> getUserEntityClass()
   {
      return MemberAccount.class;
   }
}
