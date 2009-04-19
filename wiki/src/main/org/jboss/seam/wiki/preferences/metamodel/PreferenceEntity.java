package org.jboss.seam.wiki.preferences.metamodel;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.hibernate.validator.NotNull;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.Validators;
import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.wiki.preferences.PreferenceValue;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/*
TODO: This implementation of Comparable is not consistent with equals()!
 */
public class PreferenceEntity implements Comparable, Serializable {

    Log log = Logging.getLog(PreferenceEntity.class);

    private Class clazz;
    private String description;
    private String entityName;
    private String mappedTo;
    private SortedSet<Property> properties = new TreeSet<Property>();
    private Map<String, Property> propertiesByName = new HashMap<String,Property>();
    private SortedSet<Property> propertiesSystemVisible = new TreeSet<Property>();
    private SortedSet<Property> propertiesUserVisible = new TreeSet<Property>();
    private SortedSet<Property> propertiesInstanceVisible = new TreeSet<Property>();

    public PreferenceEntity(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Preferences.class)) {
            throw new RuntimeException("Configured as preferences but missing @Preferences: " + entityClass.getName());
        }

        this.clazz = entityClass;
        this.description = interpolate(entityClass.getAnnotation(Preferences.class).description());
        this.entityName = entityClass.getAnnotation(Preferences.class).name();
        this.mappedTo = entityClass.getAnnotation(Preferences.class).mappedTo();
        if (this.entityName != null && this.entityName.length() == 0) this.entityName = clazz.getSimpleName();

        // @PreferenceProperty fields
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(PreferenceProperty.class)) {
                if (!field.isAccessible()) field.setAccessible(true);

                Property property =
                    new Property(field.getName(),
                                 field.getType(),
                                 interpolate(field.getAnnotation(PreferenceProperty.class).description()),
                                 field.getAnnotation(PreferenceProperty.class).visibility(),
                                 field.getAnnotation(PreferenceProperty.class).editorIncludeName(),
                                 field.getAnnotation(PreferenceProperty.class).templateComponentName(),
                                 field.getAnnotation(PreferenceProperty.class).mappedTo()
                    );

                if (property.isSystemVisible()) propertiesSystemVisible.add(property);
                if (property.isUserVisible()) propertiesUserVisible.add(property);
                if (property.isInstanceVisible()) propertiesInstanceVisible.add(property);
                properties.add(property);
                propertiesByName.put(property.getFieldName(), property);
            }
        }
    }

    public Class getClazz() {
        return clazz;
    }

    public String getDescription() {
        return description;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getMappedTo() {
        return mappedTo;
    }

    public SortedSet<Property> getProperties() {
        return properties;
    }

    public Map<String, Property> getPropertiesByName() {
        return propertiesByName;
    }

    public SortedSet<Property> getPropertiesSystemVisible() {
        return propertiesSystemVisible;
    }

    public SortedSet<Property> getPropertiesUserVisible() {
        return propertiesUserVisible;
    }

    public SortedSet<Property> getPropertiesInstanceVisible() {
        return propertiesInstanceVisible;
    }

    public boolean isSystemPropertiesVisible() {
        return propertiesSystemVisible.size() != 0;
    }

    public boolean isUserPropertiesVisible() {
        return propertiesUserVisible.size() != 0;
    }

    public boolean isInstancePropertiesVisible() {
        return propertiesInstanceVisible.size() != 0;
    }

    public Object materialize(Set<PreferenceValue> valueHolders) {
        log.trace("materializing preference entity instance: " + getEntityName());
        Map<Property, InvalidValue[]> invalidProperties = validate(valueHolders);
        if (invalidProperties.size() > 0) {
            log.error("can't materialize preference entity: " + getEntityName());
            for (Map.Entry<Property, InvalidValue[]> invalidProperty : invalidProperties.entrySet()) {
                log.error("invalid value for : " + invalidProperty.getKey());
                for (InvalidValue invalidValue : invalidProperty.getValue()) {
                    log.error("validation error: " + invalidValue.getMessage());
                }
            }
            throw new RuntimeException("could not materialize preference entity '"
                                        + getEntityName() + "', check the log");
        }

        Object preferenceEntityInstance;
        try {
            preferenceEntityInstance = getClazz().newInstance();
            for (PreferenceValue valueHolder : valueHolders) {
                log.trace("writing preference value: " + valueHolder.getPreferenceProperty().getFieldName());
                valueHolder.getPreferenceProperty()
                            .write(preferenceEntityInstance, valueHolder.getValue());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return preferenceEntityInstance;
    }

    public Map<Property, InvalidValue[]> validate(Collection<PreferenceValue> valueHolders) {
        return validate(valueHolders, Arrays.asList(PreferenceVisibility.values()));
    }

    public Map<Property, InvalidValue[]> validate(Collection<PreferenceValue> valueHolders, List<PreferenceVisibility> visibilities) {
        log.trace("validating preference entity values");

        Set<Property> uncheckedProperties = getProperties();

        Map<Property, InvalidValue[]> invalidProperties = new HashMap<Property, InvalidValue[]>();
        for (PreferenceValue valueHolder : valueHolders) {
            Property property = valueHolder.getPreferenceProperty();
            log.trace("validating " + property);
            uncheckedProperties.remove(property);
            InvalidValue[] invalidValues = property.validate(valueHolder.getValue());
            if (invalidValues.length > 0) invalidProperties.put(property, invalidValues);
        }

        log.trace("validating properties with no values, checking for @NotNull by given visibility");
        // Now validate all the properties for which we didn't have values, they need to be !@NotNull
        if (uncheckedProperties.size() > 0) {
            boolean uncheckedAreNullable = true;
            for (Property uncheckedProperty : uncheckedProperties) {
                if (uncheckedProperty.getVisibility().containsAll(visibilities) && !uncheckedProperty.isNullable()) {
                    log.error("missing value for @NotNull " + uncheckedProperty);
                    uncheckedAreNullable = false;
                }
            }
            if (!uncheckedAreNullable) {
                throw new IllegalStateException("missing values for validation of " + getEntityName() + ", check the log");
            }
        }

        return invalidProperties;
    }

    public int compareTo(Object o) {
        return getDescription().compareTo(((PreferenceEntity) o).getDescription());
    }

    public String toString() {
        return "PreferenceEntity: " + getClazz().getName() + " properties: " + getProperties().size();
    }

    /*
    TODO: This implementation of Comparable is not consistent with equals()!
     */
    public class Property implements Comparable, Serializable {

        private String fieldName;
        private Class fieldType;
        private String description;
        private List<PreferenceVisibility> visibility;
        private String editorIncludeName;
        private String templateComponentName;
        private String mappedTo;

        public Property(String fieldName, Class fieldType, String description,
                        PreferenceVisibility[] visibility,
                        String editorIncludeName, String templateComponentName, String mappedTo) {
            this.fieldName = fieldName;
            this.fieldType = fieldType;
            this.description = description;
            this.visibility = Arrays.asList(visibility);
            this.editorIncludeName = editorIncludeName;
            this.templateComponentName = templateComponentName;
            this.mappedTo = mappedTo;
        }

        public Field getField() {
            return Reflections.getField(getClazz(), fieldName);
        }

        public String getOwningEntityName() {
            return getEntityName();
        }

        public String getFieldName() {
            return fieldName;
        }

        public Class getFieldType() {
            return fieldType;
        }

        public String getDescription() {
            return description;
        }

        public List<PreferenceVisibility> getVisibility() {
            return visibility;
        }

        public String getEditorIncludeName() {
            return "editor" + editorIncludeName;
        }

        public String getTemplateComponentName() {
            return templateComponentName;
        }

        public String getMappedTo() {
            return mappedTo;
        }

        public boolean isSystemVisible() {
            return getVisibility().contains(PreferenceVisibility.SYSTEM);
        }

        public boolean isUserVisible() {
            return getVisibility().contains(PreferenceVisibility.USER);
        }

        public boolean isInstanceVisible() {
            return getVisibility().contains(PreferenceVisibility.INSTANCE);
        }

        public void write(Object preferenceEntityInstance, Object value) throws Exception {
            Field field = getField();
            field.setAccessible(true);
            Reflections.set(field, preferenceEntityInstance, value);
        }

        public boolean isNullable() {
            return !getField().isAnnotationPresent(NotNull.class);
        }

        public InvalidValue[] validate(Object value) {
            ClassValidator validator = Validators.instance().getValidator(getClazz());
            return validator.getPotentialInvalidValues(getFieldName(), value);
        }

        public int compareTo(Object o) {
            return getDescription().compareTo(((Property) o).getDescription());
        }

        public String toString() {
            return "Property: " + getFieldName() + " of type: " + getFieldType();
        }
    }

    private String interpolate(String s) {
        return Interpolator.instance().interpolate(s);
    }

}