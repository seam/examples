/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.web.Filter;
import org.jboss.seam.web.AbstractFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Removes jsessionid from encoded URLs, see http://jira.jboss.com/jira/browse/JBSEAM-3018
 *
 * More details: http://foreverprecio.us/groovy_tech
 *
 * @author Christian Bauer
 */
@Startup
@Scope(ScopeType.APPLICATION)
@Name("wikiUrlSessionIdFilter")
@BypassInterceptors
@Filter
public class WikiUrlSessionIdFilter extends AbstractFilter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        if (!(req instanceof HttpServletRequest)) {
            chain.doFilter(req, res);
            return;
        }

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Redirect requests with JSESSIONID in URL to clean version (old links bookmarked/stored by bots)
        // This is ONLY triggered if the request did not also contain a JSESSIONID cookie! Which should be fine for bots...
        if (request.isRequestedSessionIdFromURL()) {
            String url = request.getRequestURL()
                         .append(request.getQueryString() != null ? "?"+request.getQueryString() : "")
                         .toString();
            // TODO: The url is clean, at least in Tomcat, which strips out the JSESSIONID path parameter automatically (Jetty does not?!)
            response.setHeader("Location", url);
            response.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY);
            return;
        }

        // Prevent rendering of JSESSIONID in URLs for all outgoing links
        HttpServletResponseWrapper wrappedResponse =
            new HttpServletResponseWrapper(response) {
                @Override
                public String encodeRedirectUrl(String url) {
                    return url;
                }

                @Override
                public String encodeRedirectURL(String url) {
                    return url;
                }

                @Override
                public String encodeUrl(String url) {
                    return url;
                }

                @Override
                public String encodeURL(String url) {
                    return url;
                }
            };
        chain.doFilter(req, wrappedResponse);

    }
}
