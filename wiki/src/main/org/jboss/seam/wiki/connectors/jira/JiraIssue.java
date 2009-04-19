/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.jira;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Field;

/**
 * @author Christian Bauer
 */
public class JiraIssue {

    private static Field[] fields = JiraIssue.class.getDeclaredFields();

    private String id;
    private String key;
    private String project;
    private String summary;
    private String description;
    private String created;
    private String updated;
    private String type;
    private Object[] affectsVersions;
    private Object[] fixVersions;
    private String status;
    private Object[] components;
    private Object[] customFieldValues;
    private String votes;
    private String priority;

    public static Field[] getFields() {
        return fields;
    }

    public static void setFields(Field[] fields) {
        JiraIssue.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object[] getAffectsVersions() {
        return affectsVersions;
    }

    public void setAffectsVersions(Object[] affectsVersions) {
        this.affectsVersions = affectsVersions;
    }

    public Object[] getFixVersions() {
        return fixVersions;
    }

    public void setFixVersions(Object[] fixVersions) {
        this.fixVersions = fixVersions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object[] getComponents() {
        return components;
    }

    public List<String> getComponentNames() {
        List<String> componentNames = new ArrayList<String>();
        for (Object o : getComponents()) {
            String componentName = (String) ((Map)o).get("name");
            componentNames.add(componentName);
        }
        return componentNames;
    }

    public void setComponents(Object[] components) {
        this.components = components;
    }

    public Object[] getCustomFieldValues() {
        return customFieldValues;
    }

    public void setCustomFieldValues(Object[] customFieldValues) {
        this.customFieldValues = customFieldValues;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getPriorityIcon() {
        int p = new Integer(getPriority());
        switch (p) {
            case 1: return "blocker";
            case 2: return "critical";
            case 3: return "major";
            case 4: return "minor";
            case 5: return "optional";
            default: return "trivial";
        }
    }

    public static JiraIssue fromMap(Map map) {
        try {
            JiraIssue issue = new JiraIssue();
            for (Field field : fields) {
                if (map.containsKey(field.getName())) {
                    field.set(issue, map.get(field.getName()));
                }
            }
            return issue;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String toString() {
        return "JiraIssue (" + getKey() + "): " + getSummary();
    }
}
