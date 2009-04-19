package org.jboss.seam.example.ui;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class BookPk implements Serializable
{
   
   private String name;
   
   private String author;
   
   protected BookPk()
   {
   }
   
   public BookPk(String name, String author)
   {
      this.name = name;
      this.author = author;
   }

   public String getName()
   {
      return name;
   }
   
   public String getAuthor()
   {
      return author;
   }
   
   @Override
   public boolean equals(Object other)
   {
      if (other instanceof BookPk)
      {
         BookPk that = (BookPk) other;
         return this.getAuthor().equals(that.getAuthor()) && this.getName().equals(that.getName());
      }
      else
      {
         return false;
      }
   }
}
