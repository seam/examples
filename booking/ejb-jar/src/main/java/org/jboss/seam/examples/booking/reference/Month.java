package org.jboss.seam.examples.booking.reference;

/**
 * A simple Java bean representing a month. This
 * bean assumes that the names it is provided
 * have already been localized.
 *
 * @author Dan Allen
 */
public class Month {

   private int index;
   private String name;
   private String shortName;

   public Month() {}

   public Month(int index, String name, String shortName)
   {
      this.index = index;
      this.name = name;
      this.shortName = shortName;
   }

   public int getIndex()
   {
      return index;
   }

   public int getNumber()
   {
      return index + 1;
   }

   public String getLongName()
   {
      return name;
   }

   public String getShortName()
   {
      return shortName;
   }

   public String getName()
   {
      return name;
   }
}
