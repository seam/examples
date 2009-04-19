/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.Component;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("uploadNodeRemover")
public class UploadNodeRemover extends NodeRemover<WikiUpload> implements Serializable {

    public boolean isRemovable(WikiUpload upload) {
        return true;
    }

    public void removeDependencies(WikiUpload upload) {
        getLog().debug("removing dependencies of: " + upload);

    }
}