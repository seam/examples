/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.util;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.seam.deployment.AbstractDeploymentHandler;
import org.jboss.seam.deployment.DeploymentMetadata;
import org.jboss.seam.deployment.FileDescriptor;
import org.jboss.seam.util.DTDEntityResolver;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.net.UnknownHostException;

/**
 * Seam deployment handler, can be configured in seam-deployment.properties.
 * <p>
 * Supports scanning of files with <tt>getExtension()</tt>, returns them as dom4j
 * <tt>Element</tt> (the root element of each XML file). Override the
 * <tt>isSchemaValidating()</tt> method to force DTD/schema validation.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class XmlDeploymentHandler extends AbstractDeploymentHandler {

    Map<String, Element> elements;
    
    private DeploymentMetadata deploymentMetadata;
    
    public XmlDeploymentHandler()
    {
        deploymentMetadata = new DeploymentMetadata() {
           
           public String getFileNameSuffix() {
               return ".plugin.xml";
           }
           
        };
    }
  
    public DeploymentMetadata getMetadata() {
        return deploymentMetadata;
    }

    public abstract String getExtension();

    public boolean isSchemaValidating() {
        return false;
    }

    public Map<String, Element> getDescriptorsAsXmlElements() {
        // Lazy access to streams
        if (elements == null) {
            elements = new HashMap<String, Element>();
            for (FileDescriptor fileDescriptor : getResources()) {
                try {
                    SAXReader saxReader = new SAXReader();
                    saxReader.setMergeAdjacentText(true);

                    if (isSchemaValidating()) {
                        saxReader.setEntityResolver(new DTDEntityResolver());
                        saxReader.setValidation(true);
                        saxReader.setFeature("http://apache.org/xml/features/validation/schema",true);
                    }

                    elements.put(fileDescriptor.getName(), saxReader.read(fileDescriptor.getUrl().openStream()).getRootElement());

                } catch (DocumentException dex) {
                    Throwable nested = dex.getNestedException();
                    if (nested != null) {
                        if (nested instanceof FileNotFoundException) {
                            throw new RuntimeException(
                                "Can't find schema/DTD reference for file: "
                                + fileDescriptor.getName() + "':  "
                                + nested.getMessage(), dex
                            );
                        } else if (nested instanceof UnknownHostException) {
                            throw new RuntimeException(
                                "Cannot connect to host from schema/DTD reference: "
                                + nested.getMessage()
                                + " - check that your schema/DTD reference is current", dex
                            );
                        }
                    }
                    throw new RuntimeException("Could not parse XML file: " + fileDescriptor.getName() ,dex);
                } catch (Exception ex) {
                    throw new RuntimeException("Could not parse XML file: " + fileDescriptor.getName() ,ex);
                }
            }
        }
        return elements;
    }

}
