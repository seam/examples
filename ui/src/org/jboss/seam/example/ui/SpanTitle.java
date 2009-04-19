package org.jboss.seam.example.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;

@Name("spanTitle")
public class SpanTitle
{

   @Unwrap
   public String unwrap()
   {
      return "A Span title";
   }

}
