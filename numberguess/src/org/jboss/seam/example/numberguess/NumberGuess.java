package org.jboss.seam.example.numberguess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("numberGuess")
@Scope(ScopeType.CONVERSATION)
public class NumberGuess implements Serializable {
   
   private int randomNumber;
   private Integer currentGuess;
   private int biggest;
   private int smallest;
   private int guessCount;
   private int maxGuesses;
   private boolean cheated;
   
   @Create
   public void begin()
   {
      randomNumber = new Random().nextInt(100) + 1;
      guessCount = 0;
      biggest = 100;
      smallest = 1;
   }
   
   public void setCurrentGuess(Integer guess)
   {
      this.currentGuess = guess;
   }
   
   public Integer getCurrentGuess()
   {
      return currentGuess;
   }
   
   public void guess()
   {
      if (currentGuess>randomNumber)
      {
         biggest = currentGuess - 1;
      }
      if (currentGuess<randomNumber)
      {
         smallest = currentGuess + 1;
      }
      guessCount ++;
   }
   
   public boolean isCorrectGuess()
   {
      return currentGuess==randomNumber;
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
   
   public boolean isLastGuess()
   {
      return guessCount==maxGuesses;
   }

   public int getRemainingGuesses() {
      return maxGuesses-guessCount;
   }

   public void setMaxGuesses(int maxGuesses) {
      this.maxGuesses = maxGuesses;
   }

   public int getMaxGuesses() {
      return maxGuesses;
   }

   public int getRandomNumber() {
      return randomNumber;
   }

   public void cheated()
   {
      cheated = true;
   }
   
   public boolean isCheat() {
      return cheated;
   }
   
   public List<Integer> getPossibilities()
   {
      List<Integer> result = new ArrayList<Integer>();
      for(int i=smallest; i<=biggest; i++) result.add(i);
      return result;
   }
   
}
