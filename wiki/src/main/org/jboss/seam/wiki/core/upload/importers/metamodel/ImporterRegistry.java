package org.jboss.seam.wiki.core.upload.importers.metamodel;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.upload.importers.annotations.UploadImporter;
import org.jboss.seam.wiki.util.AnnotationDeploymentHelper;

import java.util.*;

@Name("importerRegistry")
@Scope(ScopeType.APPLICATION)
@Startup
@BypassInterceptors

public class ImporterRegistry {

    private static final LogProvider log = Logging.getLogProvider(ImporterRegistry.class);

    SortedMap<String, UploadImporter> importerComponents = new TreeMap<String, UploadImporter>();

    @Create
    public void startup() {

        log.debug("initializing upload importer registry");

        Set<Class<?>> importerClasses = AnnotationDeploymentHelper.getAnnotatedClasses(UploadImporter.class);
        if (importerClasses == null) return;

        for (Class<?> importerClass : importerClasses) {

            importerComponents.put(
                importerClass.getAnnotation(Name.class).value(),
                importerClass.getAnnotation(UploadImporter.class)
            );
            log.debug("added upload importer to registry: " + importerClass.getAnnotation(Name.class).value());
        }
    }

    public SortedMap<String, UploadImporter> getImporterComponents() {
        return importerComponents;
    }

    public List<String> getImporterComponentNames() {
        return new ArrayList(importerComponents.keySet());
    }

    public List<String> getAvailableImporters(String mimeType, String extension) {
        List<String> availableImporters = new ArrayList<String>();
        for (Map.Entry<String, UploadImporter> importerEntry : importerComponents.entrySet()) {
            List<String> supportedMimeTypes = Arrays.asList(importerEntry.getValue().handledMimeTypes());
            List<String> supportedExtensions = Arrays.asList(importerEntry.getValue().handledExtensions());
            if (supportedMimeTypes.contains(mimeType) && supportedExtensions.contains(extension) ) {
                availableImporters.add(importerEntry.getKey());
            }
        }
        return availableImporters;
    }

}
