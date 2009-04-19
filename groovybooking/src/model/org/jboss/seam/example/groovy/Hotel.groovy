//$Id$
package org.jboss.seam.example.groovy

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

import org.hibernate.validator.Length
import org.hibernate.validator.NotNull
import org.jboss.seam.annotations.Name

@Entity
@Name("hotel")
class Hotel implements Serializable
{
   @Id @GeneratedValue
   Long id

   @Length(max=50) @NotNull
   String name

   @Length(max=100) @NotNull
   String address

   @Length(max=40) @NotNull
   String city

   @Length(min=2, max=10) @NotNull
   String state

   @Length(min=4, max=6) @NotNull
   String zip

   @Length(min=2, max=40) @NotNull
   String country

   @Column(precision=6, scale=2)
   BigDecimal price

   @Override
   String toString()
   {
      return "Hotel(${name},${address},${city},${zip})"
   }
}
