/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import com.sun.facelets.impl.DefaultResourceResolver;

import java.net.URL;

/**
 * Utitility to load Facelets XHTML files as a resource from the classpath.
 *
 * @author Christian Bauer
 */
public class WikiFaceletsResourceResolver extends DefaultResourceResolver {

    public URL resolveUrl(String path) {
        // First try the regular resolver
        URL url = super.resolveUrl(path);
        if (url != null) return url;
        // Only if we can't find it check in the classpath
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return loader.getResource(path.startsWith("/") ? path.substring(1) : path);
    }
}
