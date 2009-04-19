/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.core.ResourceLoader;

import java.util.List;
import java.util.Arrays;

/**
 * Override the stateless built-in Seam component and provide a custom
 * list of bundle names to load, using the <tt>pluginMessageBundleNames</tt>
 * and a static list of core bundles.
 *
 * @author Christian Bauer
 */
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Install(precedence=Install.APPLICATION)
@Name("org.jboss.seam.core.resourceLoader")
public class WikiResourceLoader extends ResourceLoader {

    protected String[] CORE_BUNDLE_NAMES = new String[] {
        "i18n.messages", "i18n.messages_feedConnector", "i18n_messags_jiraConnector"
    };

    public String[] getBundleNames() {
        List<String> bundleNames = (List<String>) Component.getInstance("pluginMessageBundleNames");
        bundleNames.addAll(Arrays.asList(CORE_BUNDLE_NAMES));
        String[] bundles = new String[bundleNames.size()];
        return bundleNames.toArray(bundles);
    }
}
