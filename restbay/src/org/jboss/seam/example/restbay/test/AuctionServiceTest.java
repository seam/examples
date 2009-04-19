package org.jboss.seam.example.restbay.test;

import org.jboss.seam.example.restbay.test.fwk.ResourceSeamTest;
import org.jboss.seam.example.restbay.test.fwk.MockHttpServletResponse;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class AuctionServiceTest extends ResourceSeamTest
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
   public void testCategories() throws Exception
   {

      new ResourceRequest(Method.GET, "/restv1/category")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            String[] lines = response.getContentAsString().split("\n");
            assert lines[0].equals("1,Antiques");
            assert lines[1].equals("2,Art");
            assert lines[2].equals("3,Books");
         }

      }.run();

      new ResourceRequest(Method.GET, "/restv1/category/1")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("Antiques");
         }

      }.run();

   }

   @Test
   public void testAuctions() throws Exception
   {

      new ResourceRequest(Method.GET, "/restv1/auction")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            // TODO: Assert content
         }

      }.run();

      new ResourceRequest(Method.GET, "/restv1/auction/19264723")
      {

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("Whistler's Mother, original painting by James McNeill Whistler");
         }

      }.run();

   }

}