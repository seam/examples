package org.jboss.seam.wiki.core.preferences;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.PreferenceProvider;
import org.jboss.seam.wiki.preferences.PreferenceValue;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;

/**
 * Implementation for the wiki, loads and stores <tt>WikiPreferenceValue</tt> objects.
 * <p>
 * This implementation tries to be as smart as possible and supports multi-level preference value overrides.
 * If you load values, they are automatically resolved for system, user, and instance levels. If a value is
 * present at system level but you want values for users, this implementation creates and returns those
 * missing values. It will also persist them if you call <tt>store()</tt> and <tt>flush()</tt>.
 * </p>
 * <p>
 * System and user-level values are loaded and stored in the database with the <tt>entityManager</tt>
 * persistence context. be careful to only flush it if you want to after you retrieved and modified
 * values with this provider.
 * </p>
 * <p>
 * Instance-level values are not stored in the database, they are marshalled by converting <tt>WikiMacro</tt>
 * (the instance this provider understands) parameters.
 * </p>
 *
 * @author Christian Bauer
 */
@Name("preferenceProvider")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class WikiPreferenceProvider implements PreferenceProvider<User, WikiPluginMacro>, Serializable {

    @Logger Log log;

    @In
    EntityManager entityManager;

    @In
    PreferenceRegistry preferenceRegistry;

    // Queue in current conversation until flush()
    List<PreferenceValue> newValueHolders = new ArrayList<PreferenceValue>();

    public Set<PreferenceValue> loadValues(String preferenceEntityName,
                                           User user, WikiPluginMacro instance,
                                           List<PreferenceVisibility> visibilities) {

        log.debug("assembling preference values for '"
                    + preferenceEntityName
                    + "' and user '" + user
                    + "' and wiki macro ' " + instance + "'");

        PreferenceEntity entity = preferenceRegistry.getPreferenceEntitiesByName().get(preferenceEntityName);
        SortedSet<PreferenceValue> valueHolders = new TreeSet<PreferenceValue>();

        Set<PreferenceValue> systemValueHolders = null;
        if (visibilities.contains(PreferenceVisibility.SYSTEM)) {
            log.trace("retrieving SYSTEM preference values from database");

            systemValueHolders = loadSystemValues(preferenceEntityName);

            // Check if all SYSTEM-level properties were present in the database
            for (PreferenceEntity.Property systemVisibleProperty : entity.getPropertiesSystemVisible()) {
                WikiPreferenceValue newValueHolder = new WikiPreferenceValue(systemVisibleProperty);
                // If not, queue a new value holder (with a null "value") for that property and return it
                if (!systemValueHolders.contains(newValueHolder)) {
                    systemValueHolders.add(newValueHolder);
                    newValueHolders.add(newValueHolder);
                }
            }
            valueHolders.addAll(systemValueHolders);
        }

        Set<PreferenceValue> userValueHolders;
        if (visibilities.contains(PreferenceVisibility.USER)) {
            if (user == null)
                throw new IllegalArgumentException("can't load preferences for null user");
            log.trace("retrieving USER preference values from database");

            userValueHolders = loadUserValues(preferenceEntityName, user);

            // We need them when we iterate through missing properties next
            if (systemValueHolders == null) systemValueHolders = loadSystemValues(preferenceEntityName);

            // Check if all USER-level properties were present in the database
            for (PreferenceEntity.Property userVisibleProperty : entity.getPropertiesUserVisible()) {
                WikiPreferenceValue newValueHolder = new WikiPreferenceValue(userVisibleProperty);

                // If not, queue a new value for that property and return it
                if (!userValueHolders.contains(newValueHolder)) {
                    log.trace("creating new preference value for user, missing in database " + userVisibleProperty);
                    userValueHolders.add(newValueHolder);
                    newValueHolders.add(newValueHolder);

                    newValueHolder.setUser(user);

                    // We need a "value" for that property, let's check if we have a SYSTEM-level property we can take it from
                    if (userVisibleProperty.getVisibility().contains(PreferenceVisibility.SYSTEM) &&
                        systemValueHolders != null && systemValueHolders.contains(newValueHolder)) {
                        for (PreferenceValue systemValue : systemValueHolders) {
                            if (systemValue.equals(newValueHolder)) {
                                log.trace("taking the initial value from the SYSTEM-level setting");
                                newValueHolder.setValue(systemValue.getValue());
                                newValueHolder.setDirty(false); // New value isn't dirty, by definition
                            }
                        }
                    }
                    // If we don't have a value, well, then it's null (this is a property with USER and no SYSTEM visibility)
                }
            }

            // Override SYSTEM values
            valueHolders.removeAll(userValueHolders);
            valueHolders.addAll(userValueHolders);
        }

        if (visibilities.contains(PreferenceVisibility.INSTANCE)) {
            if (instance == null)
                throw new IllegalArgumentException("can't load preferences for null WikiMacro instance");
            log.trace("extracting INSTANCE preference values from " + instance);
            Set<PreferenceValue> instanceValues = loadInstanceValues(preferenceEntityName, instance);
            // Override SYSTEM and USER values
            valueHolders.removeAll(instanceValues);
            valueHolders.addAll(instanceValues);
        }

        return valueHolders;
    }

    public void storeValues(Set<PreferenceValue> valueHolders, User user, WikiPluginMacro instance) {
        // TODO: We don't care about the arguments, maybe instance later on when we marshall stuff into WikiMacro params

        // The new ones need to be checked if they are dirty and manually persisted
        for (PreferenceValue newPreferenceValue : newValueHolders) {
            if (newPreferenceValue.isDirty()) {
                log.debug("storing new preference value " + newPreferenceValue);
                entityManager.persist(newPreferenceValue);
            }
        }

        // The old ones are automatically checked for dirty state by Hibernate

    }

    public void deleteUserPreferenceValues(User user) {
        log.debug("deleting preferences of user: " + user);
        List<WikiPreferenceValue> values =
            entityManager.createQuery(
                            "select wp from WikiPreferenceValue wp" +
                            " where wp.user = :user"
                          ).setParameter("user", user)
                           .getResultList();
        for (WikiPreferenceValue value : values) {
            entityManager.remove(value);
        }
    }

    public void flush() {
        log.debug("flushing preference provider");
        entityManager.flush();

        // Reset queue
        newValueHolders = new ArrayList<PreferenceValue>();
    }


    /* ######################################### IMPLEMENTATION DETAILS ######################################### */

    private Set<PreferenceValue> loadSystemValues(String entityName) {
        List<WikiPreferenceValue> values =
            entityManager.createQuery(
                            "select wp from WikiPreferenceValue wp" +
                            " where wp.entityName = :name and wp.user is null"
                          ).setParameter("name", entityName)
                           .setHint("org.hibernate.cacheable", true)
                           .getResultList();
        setPropertyReferences(values, entityName, PreferenceVisibility.SYSTEM);
        return new HashSet(values);
    }

    private Set<PreferenceValue> loadUserValues(String entityName, User user) {
        if (user.getId() == null) return Collections.EMPTY_SET;
        List<WikiPreferenceValue> values =
            entityManager.createQuery(
                            "select wp from WikiPreferenceValue wp" +
                            " where wp.entityName = :name and wp.user = :user"
                          ).setParameter("name", entityName)
                           .setParameter("user", user)
                           .setHint("org.hibernate.cacheable", true)
                           .getResultList();
        setPropertyReferences(values, entityName, PreferenceVisibility.USER);
        return new HashSet(values);
    }

    private Set<PreferenceValue> loadInstanceValues(String entityName, WikiPluginMacro instance) {
        Set<PreferenceValue> valueHolders = new HashSet<PreferenceValue>();
        PreferenceEntity preferenceEntity = preferenceRegistry.getPreferenceEntitiesByName().get(entityName);
        for (Map.Entry<String, String> entry : instance.getParams().entrySet()) {
            log.trace("converting WikiMacro parameter into WikiPreferenceValue: " + entry.getKey());

            // TODO: Maybe we should log the following as DEBUG level, these occur when the user edits macro parameters

            PreferenceEntity.Property property = preferenceEntity.getPropertiesByName().get(entry.getKey());
            if (property == null) {
                log.info("can't convert unknown property as WikiMacro parameter: " + entry.getKey());
                continue;
            }
            if (!property.getVisibility().contains(PreferenceVisibility.INSTANCE)) {
                log.info("can't convert WikiMacro parameter, not overridable at INSTANCE level: " + entry.getKey());
                continue;
            }
            if (!instance.getMetadata().getParameters().contains(property)) {
                log.info("can't convert WikiMacro parameter, property is not configured: " + entry.getKey());
                continue;
            }
            WikiPreferenceValue value = new WikiPreferenceValue(property, entry.getValue());
            if (value.getValue() != null) {
                log.trace("converted WikiMacro parameter value into WikiPreferenceValue: " + value.getValue());
                valueHolders.add(value);
            } else {
                log.info("could not convert WikiMacro parameter value into WikiPreferenceValue: " + entry.getKey());
            }
        }
        return valueHolders;
    }

    private void setPropertyReferences(List<WikiPreferenceValue> valueHolders, String entityName, PreferenceVisibility visibility) {
        Iterator<WikiPreferenceValue> it = valueHolders.iterator();
        while (it.hasNext()) {
            WikiPreferenceValue wikiPreferenceValue = it.next();
            if (!setPropertyReference(wikiPreferenceValue, entityName, visibility)) {
                 it.remove(); // Kick out the value if we couldn't set a reference to the metamodel property
            }
        }
    }

    private boolean setPropertyReference(WikiPreferenceValue value, String entityName, PreferenceVisibility visibility) {
        PreferenceEntity entity = preferenceRegistry.getPreferenceEntitiesByName().get(entityName);
        if (entity == null) {
            log.warn("orphaned preference value found in database, please clean up: " + value);
            return false;
        }
        PreferenceEntity.Property property = entity.getPropertiesByName().get(value.getPropertyName());
        if (property == null) {
            log.warn("orphaned preference value found in database, please clean up: " + value);
            return false;
        }
        if (!property.getVisibility().contains(visibility)) {
            log.warn("database visibility " + visibility  + " is not allowed by property, please clean up: " + value);
            return false;
        }
        value.setPreferenceProperty(property);
        return true;
    }


}
