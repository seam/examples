/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.ScopeType;
import org.jboss.seam.util.EnumerationEnumeration;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.web.AbstractFilter;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Collections;
import java.util.Map;

/**
 * Adapts the Tuckey URLRewrite filter to the Seam filter chain.
 *
 * @author Christian Bauer
 */
@Startup
@Scope(ScopeType.APPLICATION)
@Name("wikiUrlRewriteFilter")
@BypassInterceptors
@Filter(within = "org.jboss.seam.web.ajax4jsfFilter")
@Install(classDependencies = "org.tuckey.web.filters.urlrewrite.UrlRewriteFilter", precedence = Install.APPLICATION)
public class WikiUrlRewriteFilter extends AbstractFilter {

    private UrlRewriteFilter urlRewriteFilter;

    public void init(FilterConfig filterConfig) throws ServletException {
        urlRewriteFilter = new UrlRewriteFilter();
        urlRewriteFilter.init(new FilterConfigWrapper(filterConfig, getInitParameters()));
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        urlRewriteFilter.doFilter(servletRequest, servletResponse, filterChain);
    }

    private Map<String, String> initParameters;

    public Map<String, String> getInitParameters() {
        return initParameters;
    }
    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }

    private class FilterConfigWrapper implements FilterConfig {

        private FilterConfig delegate;
        private Map<String, String> parameters;

        public FilterConfigWrapper(FilterConfig filterConfig, Map<String, String> initParameters) {
            delegate = filterConfig;
            parameters = initParameters;
        }

        public String getFilterName() {
            return delegate.getFilterName();
        }

        public String getInitParameter(String name) {
            if (parameters.containsKey(name)) {
                return parameters.get(name);
            } else {
                return delegate.getInitParameter(name);
            }
        }

        public Enumeration getInitParameterNames() {
            Enumeration[] enumerations = {delegate.getInitParameterNames(), Collections.enumeration(parameters.keySet())};
            return new EnumerationEnumeration(enumerations);
        }

        public ServletContext getServletContext() {
            return delegate.getServletContext();
        }
    }

}
