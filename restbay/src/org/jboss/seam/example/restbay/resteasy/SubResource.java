package org.jboss.seam.example.restbay.resteasy;

import javax.ws.rs.GET;

/**
 * @author Christian Bauer
 */
public class SubResource
{
   private String baz;

   public SubResource(String baz)
   {
      this.baz = baz;
   }

   @GET
   public String get() {
      return "bar: " + baz;
   }
}
