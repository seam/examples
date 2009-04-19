package org.jboss.seam.wiki.core.upload.importers.metamodel;

import org.jboss.seam.wiki.core.model.WikiUpload;

import javax.persistence.EntityManager;

public interface Importer {

    public boolean handleImport(EntityManager entityManager, WikiUpload file);

}
