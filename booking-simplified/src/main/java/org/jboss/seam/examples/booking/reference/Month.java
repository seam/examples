/* 
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
