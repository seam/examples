//$Id$
package org.jboss.seam.example.todo.test;

import java.util.List;

import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.TaskInstanceList;
import org.jboss.seam.mock.SeamTest;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.testng.annotations.Test;

public class TodoTest extends SeamTest
{
   
   private long taskId;
   
   @Test
   public void testTodo() throws Exception
   {
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{login.user}", "gavin");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            assert invokeMethod("#{login.login}").equals("/todo.jsp");
            assert Actor.instance().getId().equals("gavin");
         }

         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) getInstance(TaskInstanceList.class) ).size()==0;
         }
         
      }.run();
      
      new FacesRequest()
      {

         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{todoList.description}", "Kick Roy out of my office");
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            invokeMethod("#{todoList.createTodo}");
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            List<TaskInstance> tasks = (List<TaskInstance>) getInstance(TaskInstanceList.class);
            assert tasks.size()==1;
            TaskInstance taskInstance = tasks.get(0);
            assert taskInstance.getDescription().equals("Kick Roy out of my office");
            taskId = taskInstance.getId();
         }
         
      }.run();

   
      new FacesRequest()
      {
   
         @Override
         protected void beforeRequest()
         {
            setParameter("taskId", Long.toString(taskId));
         }

         @Override
         protected void invokeApplication() throws Exception
         {
            invokeMethod("#{todoList.done}");
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert ( (List) getInstance(TaskInstanceList.class) ).size()==0;
         }
         
      }.run();
   }
   
}
