package org.jboss.seam.international.examples.timeanddate.locale;

import java.util.List;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.faces.convert.Converter;
import javax.inject.Inject;

/**
 * Faces Converter for java.util.Locale
 * @author <a href="http://community.jboss.org/people/maschmid">Marek Schmidt</a>
 */
@FacesConverter("localeConverter")
public class LocaleConverter implements Converter {

    @Inject
    List<Locale> availableLocales;
    
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String localeString) {
        for (Locale locale : availableLocales) {
            if (locale.toString().equals(localeString)) {
                return locale;
            }
        }
        
        return null;
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object locale) {
        return locale.toString();
    }
    
}
