package org.jboss.seam.example.mail;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.Component;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

public class MailSenderServlet extends HttpServlet
{
   
   @Override
   protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException
   {
      new ContextualHttpServletRequest(req)
      {

         @Override
         public void process() throws Exception
         {
            Person person = (Person) Component.getInstance(Person.class);
            String firstname = req.getParameter("name");
            String email = req.getParameter("email");
            person.setAddress(email);
            person.setFirstname(firstname);
            MailExample mailExample = (MailExample) Component.getInstance(MailExample.class);
            mailExample.sendPlain();
            resp.getWriter().write("Email sent successfully");
            resp.setStatus(200);
         }
         
      }.run();
   }
   
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      doGet(req, resp);
   }

}
