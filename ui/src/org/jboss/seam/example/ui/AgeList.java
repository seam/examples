package org.jboss.seam.example.ui;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;

@Name("ageList")
public class AgeList
{
   
   @Factory("ages")
   public int[] getAges() {
      int[] ages = {18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30};
      return ages;
   }

}
