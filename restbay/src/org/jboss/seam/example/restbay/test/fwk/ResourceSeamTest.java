package org.jboss.seam.example.restbay.test.fwk;

import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.servlet.SeamResourceServlet;
import org.testng.annotations.BeforeClass;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.util.*;
import java.io.IOException;
import java.security.Principal;

/**
 * @author Christian Bauer
 */
public class ResourceSeamTest extends SeamTest
{

   public enum Method
   {
      GET, PUT, POST, DELETE, HEAD, OPTIONS
   }

   protected SeamResourceServlet resourceServlet;

   @BeforeClass
   public void initResourceServlet() throws Exception
   {
      resourceServlet = new SeamResourceServlet();
      resourceServlet.init(
            new ServletConfig()
            {
               public String getServletName()
               {
                  return "Seam Resource Servlet";
               }

               public ServletContext getServletContext()
               {
                  return servletContext;
               }

               public String getInitParameter(String s)
               {
                  return null;
               }

               public Enumeration getInitParameterNames()
               {
                  return null;
               }
            }
      );

   }

   public abstract class ResourceRequest
   {

      private Method httpMethod;
      private String requestPath;
      private MockHttpServletRequest request;
      private MockHttpServletResponse response;

      protected ResourceRequest(Method httpMethod, String requestPath)
      {
         this.httpMethod = httpMethod;
         this.requestPath = getServletPath() + (requestPath.startsWith("/") ? requestPath : "/" + requestPath);
      }

      public void run() throws Exception
      {
         init();
         prepareRequest(request);
         seamFilter.doFilter(request, response, new FilterChain()
         {
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException
            {
               resourceServlet.service(request, response);
            }
         });
         seamFilter.destroy();
         onResponse(getResponse());
      }

      protected void init()
      {
         request = createRequest();
         response = createResponse();

         request.setMethod(httpMethod.toString());
         request.setRequestURI(requestPath);

         request.setServletPath(getServletPath());

         request.setCookies(getCookies().toArray(new Cookie[getCookies().size()]));

         for (Map.Entry<String, Object> entry : getDefaultHeaders().entrySet())
         {
            request.addHeader(entry.getKey(), entry.getValue());
         }

         request.setUserPrincipal(
               new Principal()
               {
                  public String getName()
                  {
                     return getPrincipalName();
                  }
               }
         );
         for (String role : getPrincipalRoles())
         {
            request.addUserRole(role);
         }

      }

      protected MockHttpServletRequest createRequest()
      {
         return new MockHttpServletRequest();
      }

      protected MockHttpServletResponse createResponse()
      {
         return new MockHttpServletResponse();
      }

      protected Map<String, String> getRequestQueryParameters()
      {
         return Collections.EMPTY_MAP;
      }

      protected List<Cookie> getCookies()
      {
         return Collections.EMPTY_LIST;
      }

      protected String getPrincipalName()
      {
         return null;
      }

      protected Set<String> getPrincipalRoles()
      {
         return Collections.EMPTY_SET;
      }

      protected void prepareRequest(MockHttpServletRequest request)
      {
      }

      protected void onResponse(MockHttpServletResponse response)
      {
      }

      public HttpServletRequest getRequest()
      {
         return request;
      }

      public MockHttpServletResponse getResponse()
      {
         return response;
      }

   }

   public String getServletPath()
   {
      return "/seam/resource";
   }

   public Map<String, Object> getDefaultHeaders() {
      return Collections.EMPTY_MAP;
   }

}
