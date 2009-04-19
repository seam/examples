package org.jboss.seam.example.restbay.resteasy;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Type;
import java.lang.annotation.Annotation;
import java.io.OutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Provider
@Produces("text/csv")
public class TestProvider implements MessageBodyWriter
{

   public boolean isWriteable(Class aClass, Type type, Annotation[] annotations, MediaType mediaType)
   {
      return List.class.isAssignableFrom(aClass);
   }

   public long getSize(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Object o, Class aClass, Type type, Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap httpHeaders, OutputStream outputStream) throws IOException, WebApplicationException
   {
      List<String[]> lines = (List<String[]>) o;
      StringBuilder cvs = new StringBuilder();
      for (String[] line : lines)
      {
         for (String field : line)
         {
            cvs.append(field).append(",");
         }
         cvs.deleteCharAt(cvs.length() - 1);
         cvs.append("\r\n");
      }
      outputStream.write(cvs.toString().getBytes());

   }
}
