package org.jboss.seam.example.wicket;

import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;

/**
 * Wicket allows you to build powerful custom components easily.
 * 
 * Here we've built generic border you can use to decorate an output
 * 
 * @author Pete Muir
 *
 */
public class OutputBorder extends Border
{

   /**
    * Create a new form input border
    * @param id Id of border component on page
    * @param label Label to add
    * @param component The component to wrap
    */
   public OutputBorder(String id, String label, WebComponent component)
   {
      super(id);
      add(new Label("label", label + ": "));
      add(component);
   }

}
