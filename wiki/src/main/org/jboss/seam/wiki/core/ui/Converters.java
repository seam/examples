/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.search.metamodel.SearchRegistry;
import org.jboss.seam.wiki.core.search.metamodel.SearchableEntity;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import java.io.Serializable;
import java.util.List;

@Name("converters")
@Scope(ScopeType.APPLICATION)
public class Converters {

    public String[] getMonthNames() {
        return new String[]{"NULL","January","February","March","April","May","June","July","August","September","October","November","December"};
    }

    @Name("searchableEntityConverter")
    @org.jboss.seam.annotations.faces.Converter(forClass = SearchableEntity.class)
    @BypassInterceptors
    public static class SearchableEntityConverter implements Converter, Serializable {

        public Object getAsObject(FacesContext arg0,
                                  UIComponent arg1,
                                  String arg2) throws ConverterException {
            if (arg2 == null) return null;
            SearchRegistry searchRegistry = (SearchRegistry)Component.getInstance(SearchRegistry.class);
            return searchRegistry.getSearchableEntitiesByName().get(arg2);
        }

        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
            if (arg2 instanceof SearchableEntity) {
                return ((SearchableEntity)arg2).getClazz().getName();
            } else {
                return null;
            }
        }
    }

    @Name("accessLevelConverter")
    @org.jboss.seam.annotations.faces.Converter(forClass = Role.AccessLevel.class)
    @BypassInterceptors
    public static class AccessLevelConverter implements Converter, Serializable {

        public Object getAsObject(FacesContext arg0,
                                  UIComponent arg1,
                                  String arg2) throws ConverterException {
            if (arg2 == null) return null;
            try {
                List<Role.AccessLevel> accessLevels = (List<Role.AccessLevel>)Component.getInstance("accessLevelsList");
                return accessLevels.get(accessLevels.indexOf(new Role.AccessLevel(Integer.valueOf(arg2), null)));
            } catch (NumberFormatException e) {
                throw new ConverterException("Cannot find selected access level", e);
            }
        }

        public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) throws ConverterException {
            if (arg2 instanceof Role.AccessLevel) {
                Role.AccessLevel accessLevel = (Role.AccessLevel)arg2;
                return accessLevel.getAccessLevel().toString();
            } else {
                return null;
            }
        }
    }

}
