/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.cache;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
public class ConnectorCacheKey<K> implements Serializable {

    private long accessTimestamp;
    private long updateTimestamp;
    private K keyValue;

    public ConnectorCacheKey(K keyValue) {
        this.keyValue = keyValue;
    }

    public long getAccessTimestamp() {
        return accessTimestamp;
    }

    public void setAccessTimestamp(long accessTimestamp) {
        this.accessTimestamp = accessTimestamp;
    }

    public long getUpdateTimestamp() {
        return updateTimestamp;
    }

    public void setUpdateTimestamp(long updateTimestamp) {
        this.updateTimestamp = updateTimestamp;
    }

    public K getKeyValue() {
        return keyValue;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectorCacheKey that = (ConnectorCacheKey) o;

        if (!keyValue.equals(that.keyValue)) return false;

        return true;
    }

    public int hashCode() {
        return keyValue.hashCode();
    }

    public String toString() {
        return keyValue.toString();
    }
}
