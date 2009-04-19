package org.jboss.seam.example.ui;

import java.util.Date;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.util.Strings;

@Name("equalityValidatorBean")
@Scope(ScopeType.SESSION)
public class EqualityValidatorBean
{

   @In
   private StatusMessages statusMessages;

   private String name;

   private Long age;

   private Date date;

   public void check()
   {
      if (Strings.isEmpty(name))
      {
         statusMessages.addToControl("name", Severity.WARN, "Enter a name!");
      }
      else
      {
         statusMessages.addToControl("name", Severity.INFO, "OK!");
      }
   }

   public void checkDate()
   {
      if (date == null)
      {
         statusMessages.addToControl("date", Severity.WARN, "Enter a date!");
      }
      else
      {
         statusMessages.addToControl("date", Severity.INFO, "OK!");
      }
   }

   public Date getDate()
   {
      return date;
   }

   public void setDate(Date date)
   {
      this.date = date;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public Long getAge()
   {
      return age;
   }

   public void setAge(Long age)
   {
      this.age = age;
   }

}
