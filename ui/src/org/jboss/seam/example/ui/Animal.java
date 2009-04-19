package org.jboss.seam.example.ui;

public class Animal
{

   public static Animal DOG = new Animal("Needs lots of exercise", "Dog");
   public static Animal CAT = new Animal("Looks after itself", "Cat");
   public static Animal GOLDFISH = new Animal("Needs a fishtank!", "Goldfish");
   public static Animal RABBIT = new Animal("Often has floppy ears", "Rabbit");
   public static Animal SNAKE = new Animal("Better make sure it doesn't bite you", "Snake");
   public static Animal PARROT = new Animal("Peices of Eight", "Parrot");
   
   private String notes;
   private String name;

   public Animal(String notes, String name)
   {
      this.notes = notes;
      this.name = name;
   }
   
   public String getNotes()
   {
      return notes;
   }
   
   public String getName()
   {
      return name;
   }
   
}
