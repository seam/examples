package org.jboss.seam.example.numberguess;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("guess")
@Scope(ScopeType.CONVERSATION)
public class Guess
{

   private Integer value;

   public void setValue(Integer guess)
   {
      this.value = guess;
   }
   
   public Integer getValue()
   {
      return value;
   }

}
