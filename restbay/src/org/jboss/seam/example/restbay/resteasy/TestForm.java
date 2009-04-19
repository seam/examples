package org.jboss.seam.example.restbay.resteasy;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;

/**
 * @author Christian Bauer
 */
public class TestForm
{

   @FormParam("foo")
   private String[] foo;

   @HeaderParam("bar")
   private String bar;

   @Override
   public String toString()
   {
      return bar + foo[0] + foo[1];
   }
}
