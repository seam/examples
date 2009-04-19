/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Map;
import java.io.Serializable;

/**
 * A dependency-free, pure domain model representation of a macro in wiki text.
 * <p>
 * An instance that only has a name has not been produced by parsing wiki text. Then
 * it would also have a position (every macro found during wiki text parsing is
 * numbered). Note that equality and hashcode is based on either name comparison,
 * or if a position is available, on name and position.
 * </p>
 *
 * @author Christian Bauer
 */
public class WikiTextMacro implements Serializable {

    private Integer position;
    private String name;
    private SortedMap<String,String> params = new TreeMap<String,String>();

    public WikiTextMacro(String name) {
        this.name = name;
    }

    public WikiTextMacro(String name, Integer position) {
        this.name = name;
        this.position = position;
    }

    public WikiTextMacro(String name, Integer position, SortedMap<String,String> params) {
        this.name = name;
        this.position = position;
        this.params = params;
    }

    public WikiTextMacro(WikiTextMacro that) {
        this(that.getName(), that.getPosition(), that.getParams());
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortedMap<String, String> getParams() {
        return params;
    }

    public void setParams(SortedMap<String, String> params) {
        this.params = params;
    }

    public String getParamValue(String paramName) {
        for (Map.Entry<String, String> param : params.entrySet()) {
            if (param.getKey().equals(paramName)) return param.getValue();
        }
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WikiTextMacro wikiMacro = (WikiTextMacro) o;

        if (!name.equals(wikiMacro.name)) return false;
        if (position != null ? !position.equals(wikiMacro.position) : wikiMacro.position != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (position != null ? position.hashCode() : 0);
        result = 31 * result + name.hashCode();
        return result;
    }

    public String toString() {
        return "WikiTextMacro (" + getPosition() + "): " + getName() + " Params: " + getParams().size();
    }

}
