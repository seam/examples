package org.jboss.seam.security.examples.id_consumer;

import java.util.Properties;

import org.jboss.solder.core.Veto;


@Veto
public class PropertyReader {
    private Properties properties;

    public PropertyReader(Properties properties) {
        super();
        this.properties = properties;
    }

    public String getString(String name, String defaultValue) {
        if (properties != null) {
            return properties.getProperty(name, defaultValue);
        }
        return defaultValue;
    }

    public int getInt(String name, int defaultValue) {
        if (properties != null && properties.containsKey(name)) {
            return Integer.parseInt(properties.getProperty(name));
        }
        return defaultValue;
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        if (properties != null && properties.containsKey(name)) {
            return Boolean.parseBoolean(properties.getProperty(name));
        }
        return defaultValue;
    }

    public String[] getStringArray(String name, String[] defaultValue) {
        if (properties != null && properties.containsKey(name)) {
            return properties.getProperty(name).split(";");
        }
        return defaultValue;
    }
}
