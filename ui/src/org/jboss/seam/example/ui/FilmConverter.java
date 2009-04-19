package org.jboss.seam.example.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Converter;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name("filmConverter")
@Converter(forClass = Film.class)
@BypassInterceptors
public class FilmConverter implements javax.faces.convert.Converter
{
   
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      
      EntityManager entityManager = (EntityManager) Component.getInstance("entityManager");
      
      if (value != null)
      {
         try
         {
            Integer id = Integer.parseInt(value);
            if (id != null)
            {
               return entityManager.find(Film.class, id);
            }
         }
         catch (NumberFormatException e)
         {
         }
      }
      return null;
   }
   
   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      if (value instanceof Film)
      {
         Film film = (Film) value;
         return film.getId().toString();
      }
      else
      {
         return null;
      }
   }
   
}
