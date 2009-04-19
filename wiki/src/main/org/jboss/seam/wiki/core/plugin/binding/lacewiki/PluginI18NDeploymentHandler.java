/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.binding.lacewiki;

import org.jboss.seam.wiki.core.plugin.metamodel.Plugin;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;
import org.jboss.seam.deployment.AbstractDeploymentHandler;
import org.jboss.seam.deployment.DeploymentMetadata;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.deployment.FileDescriptor;
import org.jboss.seam.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds (during startup scanning) all plugin messages files, using the pattern
 * <tt>[package]/[specialPluginI18Npackagename]/messages_[pluginKey]_[locale].properties</tt>.
 * Any found file name is converted into a resource bundle name and added to an internal
 * list. Other components can then read this list by getting an instance of this deployment
 * handler, during startup.
 *
 * @see PluginI18NBinder
 *
 * @author Christian Bauer
 */
public class PluginI18NDeploymentHandler extends AbstractDeploymentHandler {

    public static final String NAME = "pluginI18NDeploymentHandler";
    public static final String MESSAGES_PATTERN =
        "^([a-zA-Z0-9/]+)"+Plugin.PACKAGE_I18N_MESSAGES+"_("+Plugin.KEY_PATTERN+")_([a-zA-Z_]+)\\.properties$";

    private static DeploymentMetadata deploymentMetadata = new DeploymentMetadata()
    {

      public String getFileNameSuffix()
      {
         return ".properties";
      }
       
    };
    
    private Pattern compiledPattern;
    
    public PluginI18NDeploymentHandler()
    {
        compiledPattern = Pattern.compile(MESSAGES_PATTERN);
    }
    
    @Override
    public void postProcess(ClassLoader classLoader) {
        for (FileDescriptor fileDescriptor : getResources())
        {
            Matcher matcher = compiledPattern.matcher(fileDescriptor.getName());
            if (matcher.matches()) {
                String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < groups.length; i++) {
                   groups[i] = matcher.group(i+1);
                }
                if (groups == null || groups.length != 3) {
                    throw new InvalidWikiConfigurationException("Deployment of i18n properties failed");
                }
                String packageName = groups[0];
                String pluginKey = groups[1];
                String locale = groups[2]; // Don't really need it here

                if (packageName.endsWith(Plugin.PACKAGE_I18N+"/")) {
                    String bundleName = packageName.replaceAll("/", ".") + "messages_" + pluginKey;
                    getMessageBundleNames().add(bundleName);
                }
            }
        }
    }

    public String getName() {
        return NAME;
    }
    
    private List<String> messageBundleNames = new ArrayList<String>();

    public List<String> getMessageBundleNames() {
        return messageBundleNames;
    }

    public static PluginI18NDeploymentHandler instance() {
        DeploymentStrategy deployment = (DeploymentStrategy) Component.getInstance("deploymentStrategy");
        return (PluginI18NDeploymentHandler) deployment.getDeploymentHandlers().get(NAME);
    }
    
    public DeploymentMetadata getMetadata()
    {
        return deploymentMetadata;
    }

}
