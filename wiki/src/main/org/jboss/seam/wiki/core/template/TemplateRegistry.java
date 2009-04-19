/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.template;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.model.WikiDocumentDefaults;
import org.jboss.seam.wiki.core.exception.InvalidWikiConfigurationException;
import org.jboss.seam.core.Interpolator;

import java.util.*;

/**
 * @author Christian Bauer
 */
@Name("templateRegistry")
@Scope(ScopeType.APPLICATION)
public class TemplateRegistry {

    @Logger
    static Log log;

    @In(
        value="#{deploymentStrategy.annotatedClasses['org.jboss.seam.wiki.core.template.WikiDocumentTemplate']}",
        required = false
    )
    Set<Class> templateClasses;

    Map<Class, String> templates = new HashMap<Class, String>();
    List<Class> templateTypes = new ArrayList<Class>();

    @Observer("Wiki.startup")
    public void create() {
        log.debug("initializing template registry");

        for (Class<?> templateClass : templateClasses) {

            String templateName = interpolate(templateClass.getAnnotation(WikiDocumentTemplate.class).value());

            if (!WikiDocumentDefaults.class.isAssignableFrom(templateClass)) {
                throw new InvalidWikiConfigurationException("Annotated @WikiDocumentTemplate class '"
                                                            + templateClass
                                                            + "' does not implement WikiDocumentDefaults interface");
            }

            log.debug("adding template class " + templateClass.getName() + " as '" + templateName + "'");
            templates.put(templateClass, templateName);
            templateTypes.add(templateClass);
        }
    }

    private String interpolate(String s) {
        return Interpolator.instance().interpolate(s);
    }

    public Map<Class, String> getTemplates() {
        return templates;
    }

    public List<Class> getTemplateTypes() {
        return templateTypes;
    }

    public static TemplateRegistry instance() {
        return (TemplateRegistry)Component.getInstance(TemplateRegistry.class);
    }
}
