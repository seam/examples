package org.jboss.seam.example.seamspace.test;

import java.util.Date;

import org.jboss.seam.core.Manager;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class RegisterTest extends SeamTest
{
   @Test
   public void testRegister() throws Exception
   {
      String cid = new FacesRequest() 
      {                 
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{register.start}") == null;
         }         
      }.run();      

      new FacesRequest("/register.xhtml", cid)
      {
         @Override 
         protected void updateModelValues() throws Exception
         {
            setValue("#{register.member.email}", "shane@test.com");
            setValue("#{register.member.firstName}", "Shane");
            setValue("#{register.member.lastName}", "Bryzak");
            setValue("#{register.member.memberName}", "shane123");
            setValue("#{register.username}", "sbryzak");
            setValue("#{register.password}", "secret");
            setValue("#{register.confirm}", "secret");
            setValue("#{register.gender}", "Male");
            setValue("#{register.member.dob}", new Date(107100000000L));                        
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{register.next}") == null;
         }          
         
      }.run();
      
      new FacesRequest("/register2.xhtml", cid)
      {         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeAction("#{register.uploadPicture}") == null;
            assert !Manager.instance().isLongRunningConversation();
         }          
         
      }.run();     
      
      new FacesRequest()
      {         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert getValue("#{identity.loggedIn}").equals(true);
            assert invokeAction("#{identity.logout}") == null;
            assert getValue("#{identity.loggedIn}").equals(false);
         }          
         
      }.run();       
   }
}
