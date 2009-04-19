package org.jboss.seam.wiki.core.preferences;

import org.hibernate.HibernateException;
import org.hibernate.Hibernate;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.engine.SessionImplementor;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Date;

public class PreferenceValueUserType implements CompositeUserType {

    public Class returnedClass() {
        return Object.class;
    }

    public String[] getPropertyNames() {
        return new String[]{
                "LONG",
                "DOUBLE",
                "TIMESTAMP",
                "BOOLEAN",
                "STRING"
        };
    }

    public Type[] getPropertyTypes() {
        return new Type[]{
                Hibernate.LONG,
                Hibernate.DOUBLE,
                Hibernate.TIMESTAMP,
                Hibernate.BOOLEAN,
                Hibernate.STRING
        };
    }

    public Object getPropertyValue(Object component, int property) throws HibernateException {
        switch (property) {
            case 0:
                return (component instanceof Long) ? component : null;
            case 1:
                return (component instanceof Double) ? component : null;
            case 2:
                return (component instanceof Date) ? component : null;
            case 3:
                return (component instanceof Boolean) ? component : null;
            case 4:
                return (component instanceof String) ? component : null;
        }
        throw new IllegalArgumentException("Preferences engine doesn't support object of type: " + component.getClass());
    }

    public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
        throw new UnsupportedOperationException();
    }


    public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
            throws HibernateException, SQLException {

        Long intValue = (Long) Hibernate.LONG.nullSafeGet(rs, names[0], session, owner);
        if (intValue != null) return intValue;

        Double doubleValue = (Double) Hibernate.DOUBLE.nullSafeGet(rs, names[1], session, owner);
        if (doubleValue != null) return doubleValue;

        Date datetimeValue = (Date) Hibernate.TIMESTAMP.nullSafeGet(rs, names[2], session, owner);
        if (datetimeValue != null) return datetimeValue;

        Boolean booleanValue = (Boolean) Hibernate.BOOLEAN.nullSafeGet(rs, names[3], session, owner);
        if (booleanValue != null) return booleanValue;

        return Hibernate.STRING.nullSafeGet(rs, names[4], session, owner);
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
            throws HibernateException, SQLException {

        Hibernate.LONG.nullSafeSet(st, (value instanceof Long) ? value : null, index);
        Hibernate.DOUBLE.nullSafeSet(st, (value instanceof Double) ? value : null, index + 1);
        Hibernate.TIMESTAMP.nullSafeSet(st, (value instanceof Date) ? value : null, index + 2);
        Hibernate.BOOLEAN.nullSafeSet(st, (value instanceof Boolean) ? value : null, index + 3);
        Hibernate.STRING.nullSafeSet(st, (value instanceof String) ? value : null, index + 4);
    }

    public boolean isMutable() {
        return false;
    }

    public boolean equals(Object x, Object y) throws HibernateException {
        return x == null ? y == null : x.equals(y);
    }

    public int hashCode(Object object) throws HibernateException {
        return object.hashCode();
    }

    public Object replace(Object original, Object target, SessionImplementor session, Object owner) {
        return original;
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
        return (Serializable) value;
    }

    public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
        return cached;
    }

}