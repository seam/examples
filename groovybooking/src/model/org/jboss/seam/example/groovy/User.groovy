//$Id$
package org.jboss.seam.example.groovy

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

import org.hibernate.validator.Length
import org.hibernate.validator.NotNull
import org.hibernate.validator.Pattern
import org.jboss.seam.ScopeType
import org.jboss.seam.annotations.Name
import org.jboss.seam.annotations.Scope

@Entity
@Name("user")
@Scope(ScopeType.SESSION)
@Table(name="Customer")
class User implements Serializable
{
   @Id
   @Length(min=5, max=15)
   @Pattern(regex=/^\w*$/, message="not a valid username")
   String username

   @NotNull
   @Length(min=5, max=15)
   String password

   @NotNull
   @Length(max=100)
   String name

   User(String name, String password, String username)
   {
      this.name = name
      this.password = password
      this.username = username
   }

   User() {}

   @Override
   String toString()
   {
      return "User(${username})"
   }
}
