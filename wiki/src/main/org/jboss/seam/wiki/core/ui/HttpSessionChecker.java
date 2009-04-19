package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.web.ServletContexts;


@Name("httpSessionChecker")
@Scope(ScopeType.APPLICATION)
public class HttpSessionChecker {

    @WebRemote
    public boolean isNewSession() {
        return ServletContexts.instance().getRequest().getSession().isNew();
    }
}
