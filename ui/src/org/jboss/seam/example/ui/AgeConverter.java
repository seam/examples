package org.jboss.seam.example.ui;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.faces.Converter;

@Name("ageConverter")
@BypassInterceptors
@Converter
public class AgeConverter implements javax.faces.convert.Converter
{
   public Object getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException
   {
      Integer i = new Integer(value);
      return i.intValue();
   }

   public String getAsString(FacesContext context, UIComponent component, Object value) throws ConverterException
   {
      return value + "";
   }
}
