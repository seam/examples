/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.binding.lacewiki;

import org.jboss.seam.Component;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.wiki.util.XmlDeploymentHandler;

/**
 * Detects all *.plugin.xml files and offers Dom4J elements.
 *
 * @author Christian Bauer
 */
public class PluginDeploymentHandler extends XmlDeploymentHandler {

    public static final String NAME = "pluginDeploymentHandler";

    public String getExtension() {
        return ".plugin.xml";
    }

    public String getName() {
        return NAME;
    }

    public boolean isSchemaValidating() {
        return true;
    }

    public static PluginDeploymentHandler instance() {
        DeploymentStrategy deployment = (DeploymentStrategy) Component.getInstance("deploymentStrategy");
        return (PluginDeploymentHandler) deployment.getDeploymentHandlers().get(NAME);
    }

}
