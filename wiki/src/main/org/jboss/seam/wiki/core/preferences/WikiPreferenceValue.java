/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.preferences;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.preferences.PreferenceValue;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;

@TypeDefs({
    @TypeDef(name="preference_value_usertype", typeClass = PreferenceValueUserType.class)
})
@Entity
@Table(name = "PREFERENCE")
/*
TODO: This implementation of Comparable MIGHT not be consistent with equals()!
 */
public class WikiPreferenceValue implements PreferenceValue, Serializable, Comparable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "PREF_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    private int version = 0;

    @Column(name = "ENTITY_NAME", nullable = false)
    private String entityName;

    @Column(name = "PROPERTY_NAME", nullable = false)
    private String propertyName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = true, updatable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_PREFERENCE_USER_ID")
    private User user;

    @org.hibernate.annotations.Type(type = "preference_value_usertype")
    @org.hibernate.annotations.Columns(
        columns = {
            @Column( name="LONG_VALUE"),
            @Column( name="DOUBLE_VALUE"),
            @Column( name="TIMESTAMP_VALUE"),
            @Column( name="BOOLEAN_VALUE"),
            @Column( name="STRING_VALUE", length = 1023)
        }
	)
    private Object value;

    @Transient
    private boolean instance = false;

    public WikiPreferenceValue() {}

    public WikiPreferenceValue(PreferenceEntity.Property property) {
        this.entityName = property.getOwningEntityName();
        this.propertyName = property.getFieldName();
        this.property = property;
    }

    public WikiPreferenceValue(PreferenceEntity.Property property, String value) {
        this.entityName = property.getOwningEntityName();
        this.propertyName = property.getFieldName();
        this.property = property;
        this.instance = true;

        if (property.getFieldType().equals(String.class)) {
            this.value = value;
        } else if (property.getFieldType().equals(Long.class)) {
            try {
                this.value = Long.valueOf(value);
            } catch (Exception ex) {}
        } else if (property.getFieldType().equals(Double.class)) {
            try {
                this.value = Double.valueOf(value);
            } catch (Exception ex) {}
        } else if (property.getFieldType().equals(Date.class)) {
            try {
                this.value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
            } catch (Exception ex) {}
        } else if (property. getFieldType().equals(Boolean.class)) {
            try {
                this.value = Boolean.valueOf(value);
            } catch (Exception ex) {}
        }
    }

    public Long getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (this.value == null && value != null)
            setDirty(true);
        else if (this.value != null && value == null)
            setDirty(true);
        else if (this.value != null && !(this.value.equals(value)))
            setDirty(true);
        this.value = value;
    }

    public boolean isSystem() {
        return user == null && !instance;
    }

    public boolean isUser() {
        return user != null && !instance;
    }

    public boolean isInstance() {
        return user == null && instance;
    }

    @Transient
    private boolean dirty;
    public boolean isDirty() { return dirty; }
    public void setDirty(boolean dirty) { this.dirty = dirty; }

    @Transient
    PreferenceEntity.Property property;

    public void setPreferenceProperty(PreferenceEntity.Property property) { this.property = property; }
    public PreferenceEntity.Property getPreferenceProperty() { return property; }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WikiPreferenceValue that = (WikiPreferenceValue) o;

        if (!entityName.equals(that.entityName)) return false;
        if (!propertyName.equals(that.propertyName)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = entityName.hashCode();
        result = 31 * result + propertyName.hashCode();
        return result;
    }

    public int compareTo(Object o) {
        return getPreferenceProperty().compareTo(((PreferenceValue)o).getPreferenceProperty());
    }

    public String toString() {
        return "WikiPreferenceValue for '" + getEntityName() + "." + getPropertyName() + "'";
    }
}