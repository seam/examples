package org.jboss.seam.example.restbay.resteasy;

import org.jboss.resteasy.annotations.Form;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

/**
 * Plain JAX RS root resource, no Seam components/lifecycle.
 * 
 * @author Christian Bauer
 */
@Path("/test")
public class TestResource
{

   @Context
   UriInfo uriInfo;

   @Context
   HttpHeaders headers;

   @GET
   @Path("/echouri")
   public String echoUri()
   {
      return uriInfo.getPath();
   }

   @GET
   @Path("/echoquery")
   public String echoQueryParam(@QueryParam("bar") String bar)
   {
      return bar;
   }

   @GET
   @Path("/echoheader")
   public String echoHeaderParam(@HeaderParam("bar") String bar)
   {
      return bar;
   }

   @GET
   @Path("/echocookie")
   public String echoCookieParam(@CookieParam("bar") String bar)
   {
      return bar;
   }

   @GET
   @Path("/echoencoded/{val}")
   public String echoEncoded(@PathParam("val") @Encoded String val)
   {
      return val;
   }

   @POST
   @Path("/echoformparams")
   @Consumes("application/x-www-form-urlencoded")
   public String echoFormParams(MultivaluedMap<String, String> formMap)
   {
      String result = "";
      for (String s : formMap.get("foo"))
      {
         result = result + s;
      }
      return result;
   }

   @POST
   @Path("/echoformparams2")
   public String echoFormParams2(@FormParam("foo") String[] foo)
   {
      String result = "";
      for (String s : foo)
      {
         result = result + s;
      }
      return result;
   }

   @POST
   @Path("/echoformparams3")
   public String echoFormParams3(@Form TestForm form)
   {
      return form.toString();
   }

   @Path("/foo/bar/{baz}")
   public SubResource getBar(@PathParam("baz") String baz)
   {
      return new SubResource(baz);
   }

   @GET
   @Path("/foo/{isoDate}")
   public long convertPathParam(@PathParam("isoDate") GregorianCalendar isoDate)
   {
      return isoDate.getTime().getTime();
   }

   @GET
   @Path("/foo/unsupported")
   public String throwException()
   {
      throw new UnsupportedOperationException("foo");
   }

   @GET
   @Path("/foo/commaseparated")
   @Produces("text/csv")
   public List<String[]> getCommaSeparated() {
      assert headers.getAcceptableMediaTypes().size() == 2;
      assert headers.getAcceptableMediaTypes().get(0).toString().equals("text/plain");
      assert headers.getAcceptableMediaTypes().get(1).toString().equals("text/csv");
      List<String[]> csv = new ArrayList();
      csv.add(new String[]{"foo", "bar"});
      csv.add(new String[]{"asdf", "123"});
      return csv;
   }


}
