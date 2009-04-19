//$Id$
package org.jboss.seam.example.messages.test;
import javax.faces.model.DataModel;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class MessageListTest extends SeamTest
{
   @Test
   public void testMessageList() throws Exception 
   {
      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            list.setRowIndex(1);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            invokeMethod("#{messageManager.select}");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            assert getValue("#{message.title}").equals("Hello World");
            assert getValue("#{message.read}").equals(true);
         }
         
      }.run();

      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==2;
            list.setRowIndex(0);
         }
         
         
         @Override
         protected void invokeApplication() throws Exception {
            invokeMethod("#{messageManager.delete}");
         }


         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==1;
         }
         
      }.run();

      new NonFacesRequest()
      {

         @Override
         protected void renderResponse() throws Exception {
            DataModel list = (DataModel) getInstance("messageList");
            assert list.getRowCount()==1;
         }
         
      }.run();

   }
   
}
