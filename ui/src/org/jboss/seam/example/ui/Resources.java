package org.jboss.seam.example.ui;

import java.io.ByteArrayInputStream;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.web.RequestParameter;

@Name("resources")
@Scope(ScopeType.EVENT)
public class Resources
{
   
   @RequestParameter
   private int id;
   
   private ResourceItem item;
   
   @Create
   public void create()
   {
      switch (id)
      {
      case 1:
         item = new ResourceItem("text.txt", new byte[] { 'a', 'b', 'c' }, null, "text/plain");
         break;
      case 2:
         ByteArrayInputStream str = new ByteArrayInputStream(new byte[] { '1', '2', '3' });
         item = new ResourceItem("numbers.txt", str, null, "text/plain");
         break;
      }
   }
   
   public static class ResourceItem
   {
      
      public ResourceItem(String fileName, Object data, String disposition, String contentType)
      {
         this.fileName = fileName;
         this.data = data;
         this.disposition = disposition;
         this.contentType = contentType;
      }
      
      public String fileName;
      public Object data;
      public String disposition;
      public String contentType;
      
      public String getFileName()
      {
         return fileName;
      }
      
      public Object getData()
      {
         return data;
      }
      
      public String getDisposition()
      {
         return disposition;
      }
      
      public String getContentType()
      {
         return contentType;
      }
      
   }
   
   public int getId()
   {
      return id;
   }
   
   public void setId(int id)
   {
      this.id = id;
   }
   
   public ResourceItem getItem()
   {
      return item;
   }
   
   public void setItem(ResourceItem item)
   {
      this.item = item;
   }
   
}
