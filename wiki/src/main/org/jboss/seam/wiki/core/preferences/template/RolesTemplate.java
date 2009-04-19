package org.jboss.seam.wiki.core.preferences.template;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.preferences.PreferenceValueTemplate;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

@Name("rolesPreferenceValueTemplate")
@Scope(ScopeType.CONVERSATION)
public class RolesTemplate implements PreferenceValueTemplate, Serializable {

    @In
    UserDAO userDAO;

    private List<String> roleNames;

    public List<String> getTemplateValues() {
        if (roleNames == null) {
            roleNames = new ArrayList<String>();
            List<Role> rolesList = (List<Role>) Component.getInstance("rolesList");
            for (Role role : rolesList) {
                roleNames.add(role.getName());
            }
        }
        return roleNames;
    }

}