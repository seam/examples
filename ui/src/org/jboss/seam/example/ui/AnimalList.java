package org.jboss.seam.example.ui;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;

@Name("animals")
public class AnimalList
{

   private List<Animal> animals;
   
   @Unwrap
   public List<Animal> unwrap()
   {
      if (animals == null)
      {
         animals = new ArrayList<Animal>();
         animals.add(Animal.CAT);
         animals.add(Animal.DOG);
         animals.add(Animal.GOLDFISH);
         animals.add(Animal.PARROT);
         animals.add(Animal.RABBIT);
         animals.add(Animal.SNAKE);
      }
      return animals;
   }
   
   
}
