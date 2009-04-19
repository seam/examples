/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.jboss.seam.deployment.AnnotationDeploymentHandler;
import org.jboss.seam.deployment.DeploymentStrategy;
import org.jboss.seam.Component;

import java.util.Set;

/**
 * A convenience class that allows us to access Seam deployment handlers without @In'jecting them.
 * <p>
 * This is useful because we can optimize things such as registries with @BypassInterceptors.
 * </p>
 *
 * @author Christian Bauer
 */
public class AnnotationDeploymentHelper {

    public static Set<Class<?>> getAnnotatedClasses(String annotationFQN) {
        DeploymentStrategy deployment = (DeploymentStrategy) Component.getInstance("deploymentStrategy");
        AnnotationDeploymentHandler handler =
                (AnnotationDeploymentHandler)deployment.getDeploymentHandlers().get(AnnotationDeploymentHandler.NAME);
        return handler.getClassMap().get(annotationFQN);
    }

    public static Set<Class<?>> getAnnotatedClasses(Class annotationType) {
        return getAnnotatedClasses(annotationType.getName());
    }

}
