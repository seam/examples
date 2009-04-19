package org.jboss.seam.example.numberguess;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("game")
@Scope(ScopeType.CONVERSATION)
public class Game
{
   
   private int biggest;
   private int smallest;
   private int guessCount;
   
   @Create 
   @Begin(pageflow="numberGuess")
   public void begin()
   {
      guessCount = 0;
      biggest = 100;
      smallest = 1;
   }
   
   public void incrementGuessCount()
   {
      guessCount++;
   }
   
   public int getBiggest()
   {
      return biggest;
   }
   
   public int getSmallest()
   {
      return smallest;
   }
   
   public int getGuessCount()
   {
      return guessCount;
   }

   public void setBiggest(int biggest)
   {
      this.biggest = biggest;
   }

   public void setSmallest(int smallest)
   {
      this.smallest = smallest;
   }
}
