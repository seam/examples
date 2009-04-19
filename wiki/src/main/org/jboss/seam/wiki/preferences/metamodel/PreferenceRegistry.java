package org.jboss.seam.wiki.preferences.metamodel;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.util.AnnotationDeploymentHelper;

import java.util.*;

@Name("preferenceRegistry")
@Scope(ScopeType.APPLICATION)
@Startup(depends = "pluginI18NBinder")
@BypassInterceptors
public class PreferenceRegistry {

    private static final LogProvider log = Logging.getLogProvider(PreferenceRegistry.class);

    Set<PreferenceEntity> preferenceEntities = new HashSet<PreferenceEntity>();
    Map<String, PreferenceEntity> preferenceEntitiesByName = new HashMap<String, PreferenceEntity>();

    Set<PreferenceEntity> preferenceEntitiesSystem = new HashSet<PreferenceEntity>();
    Set<PreferenceEntity> preferenceEntitiesUser = new HashSet<PreferenceEntity>();
    Set<PreferenceEntity> preferenceEntitiesInstance = new HashSet<PreferenceEntity>();

    @Create
    public void startup() {
        log.debug("initializing preferences registry");

        Set<Class<?>> preferencesClasses = AnnotationDeploymentHelper.getAnnotatedClasses(Preferences.class);

        if (preferencesClasses == null)
            throw new RuntimeException("No preference entities found, add @Preferences annotation to META-INF/seam-deployment.properties");

        for (Class preferencesClass : preferencesClasses) {
            PreferenceEntity preferenceEntity = new PreferenceEntity(preferencesClass);

            log.debug("adding '" + preferenceEntity.getEntityName() + "', " + preferenceEntity);

            if (preferenceEntitiesByName.containsKey(preferenceEntity.getEntityName())) {
                throw new RuntimeException("Duplicate preference entity name: " + preferenceEntity.getEntityName());
            }

            preferenceEntities.add(preferenceEntity);
            preferenceEntitiesByName.put(preferenceEntity.getEntityName(), preferenceEntity);

            if (preferenceEntity.isSystemPropertiesVisible())
                preferenceEntitiesSystem.add(preferenceEntity);
            if (preferenceEntity.isUserPropertiesVisible())
                preferenceEntitiesUser.add(preferenceEntity);
            if (preferenceEntity.isInstancePropertiesVisible())
                preferenceEntitiesInstance.add(preferenceEntity);
        }

        log.info("registered preference entities: " + preferenceEntities.size());

    }

    public Set<PreferenceEntity> getPreferenceEntities() {
        return preferenceEntities;
    }

    public Map<String, PreferenceEntity> getPreferenceEntitiesByName() {
        return preferenceEntitiesByName;
    }

    public Set<PreferenceEntity> getPreferenceEntitiesSystem() {
        return preferenceEntitiesSystem;
    }

    public Set<PreferenceEntity> getPreferenceEntitiesUser() {
        return preferenceEntitiesUser;
    }

    public Set<PreferenceEntity> getPreferenceEntitiesInstance() {
        return preferenceEntitiesInstance;
    }

    public SortedSet<PreferenceEntity> getPreferenceEntities(PreferenceVisibility... visibilities) {
        if (visibilities == null) return null;
        SortedSet<PreferenceEntity> entities = new TreeSet<PreferenceEntity>();
        List<PreferenceVisibility> visibilityList = Arrays.asList(visibilities);
        if (visibilityList.contains(PreferenceVisibility.SYSTEM)) entities.addAll(getPreferenceEntitiesSystem());
        if (visibilityList.contains(PreferenceVisibility.USER)) entities.addAll(getPreferenceEntitiesUser());
        if (visibilityList.contains(PreferenceVisibility.INSTANCE)) entities.addAll(getPreferenceEntitiesInstance());
        return entities;
    }

    public static PreferenceRegistry instance() {
        return (PreferenceRegistry) Component.getInstance(PreferenceRegistry.class);
    }

}