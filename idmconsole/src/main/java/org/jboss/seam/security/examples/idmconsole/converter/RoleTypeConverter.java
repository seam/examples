package org.jboss.seam.security.examples.idmconsole.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.picketlink.idm.api.RoleType;
import org.picketlink.idm.impl.api.model.SimpleRoleType;

@FacesConverter("roleTypeConverter")
public class RoleTypeConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        return new SimpleRoleType(arg2);
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        return ((RoleType) arg2).getName();
    }
}
