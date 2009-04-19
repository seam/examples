/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.metamodel;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
public class PluginInfo implements Serializable {

    private String description;
    private String version;
    private ApplicationVersion applicationVersion;
    private Vendor vendor;

    public class ApplicationVersion implements Serializable {
        private String min;
        private String max;

        public ApplicationVersion(String min, String max) {
            this.min = min;
            this.max = max;
        }

        public String getMin() {
            return min;
        }

        public String getMax() {
            return max;
        }
    }

    public class Vendor implements Serializable {
        private String name;
        private String url;

        public Vendor(String name, String url) {
            this.name = name;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }

    public void setApplicationVersion(String min, String max) {
        this.applicationVersion = new ApplicationVersion(min, max);
    }

    public void setVendor(String name, String url) {
        this.vendor = new Vendor(name, url);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ApplicationVersion getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(ApplicationVersion applicationVersion) {
        this.applicationVersion = applicationVersion;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
}
