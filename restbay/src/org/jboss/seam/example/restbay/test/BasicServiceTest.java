package org.jboss.seam.example.restbay.test;

import org.jboss.seam.example.restbay.test.fwk.ResourceSeamTest;
import org.jboss.seam.example.restbay.test.fwk.MockHttpServletResponse;
import org.jboss.seam.example.restbay.test.fwk.MockHttpServletRequest;
import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;

import javax.servlet.http.Cookie;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.HashMap;

public class BasicServiceTest extends ResourceSeamTest
{

   @Override
   public Map<String, Object> getDefaultHeaders()
   {
      return new HashMap<String, Object>()
      {{
            put("Accept", "text/plain");
      }};
   }

   @Test
   public void testExeptionMapping() throws Exception
   {
      new ResourceRequest(Method.GET, "/restv1/test/foo/unsupported")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 501;
            assert response.getStatusMessage().equals("The request operation is not supported: foo");
         }

      }.run();

   }

   @Test
   public void testPlainResources() throws Exception
   {
      new ResourceRequest(Method.GET, "/restv1/test/echouri")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("/test/echouri");
         }

      }.run();

      new ResourceRequest(Method.GET, "/restv1/test/echoquery")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            request.setQueryString("asdf=123");
            request.addQueryParameter("bar", "bbb");
            request.addQueryParameter("baz", "bzzz");
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("bbb");
         }

      }.run();

      new ResourceRequest(Method.GET, "/restv1/test/echoheader")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            request.addHeader("bar", "baz");
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("baz");
         }

      }.run();

      new ResourceRequest(Method.GET, "/restv1/test/echocookie")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            request.addCookie(new Cookie("bar", "baz"));
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("baz");
         }

      }.run();

      new ResourceRequest(Method.GET, "/restv1/test/foo/bar/asdf")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {

            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("bar: asdf");
         }

      }.run();

   }

   @Test
   public void testEncoding() throws Exception
   {
      new ResourceRequest(Method.GET, "/restv1/test/echoencoded/foo bar")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("foo%20bar");
         }

      }.run();
   }

   @Test
   public void testFormHandling() throws Exception
   {
      new ResourceRequest(Method.POST, "/restv1/test/echoformparams")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            request.addParameter("foo", new String[]{"bar", "baz"});
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("barbaz");
         }

      }.run();

      new ResourceRequest(Method.POST, "/restv1/test/echoformparams2")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            request.addParameter("foo", new String[]{"bar", "baz"});
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("barbaz");
         }

      }.run();

      new ResourceRequest(Method.POST, "/restv1/test/echoformparams3")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            request.addHeader("bar", "foo");
            request.addParameter("foo", new String[]{"bar", "baz"});
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("foobarbaz");
         }

      }.run();

   }

   @Test
   public void testStringConverter() throws Exception
   {
      final String ISO_DATE = "2007-07-10T14:54:56-0500";
      final String ISO_DATE_MILLIS = "1184097296000";

      new ResourceRequest(Method.GET, "/restv1/test/foo/" + ISO_DATE)
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assertEquals(response.getContentAsString(), ISO_DATE_MILLIS);
         }

      }.run();

   }

   @Test
   public void testProvider() throws Exception
   {

      new ResourceRequest(Method.GET, "/restv1/test/foo/commaseparated")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            request.addHeader("Accept", "text/csv");
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("foo,bar\r\nasdf,123\r\n");
         }

      }.run();

   }
}
