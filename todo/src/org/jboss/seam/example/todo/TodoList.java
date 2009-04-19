package org.jboss.seam.example.todo;

import org.jboss.seam.annotations.bpm.CreateProcess;
import org.jboss.seam.annotations.bpm.EndTask;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.bpm.StartTask;

@Name("todoList")
public class TodoList 
{
   
   private String description;
   
   public String getDescription()
   {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
   
   @CreateProcess(definition="todo")
   public void createTodo() {}
   
   @StartTask @EndTask
   public void done() {}

}
