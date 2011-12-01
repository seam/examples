package org.jboss.seam.security.examples.idmconsole.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.picketlink.idm.api.Group;
import org.picketlink.idm.impl.api.model.SimpleGroup;

@FacesConverter("groupConverter")
public class GroupConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        return new SimpleGroup(arg2, "GROUP");
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        return ((Group) arg2).getName();
    }

}
