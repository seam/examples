/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.plugin.metamodel;

/**
 * @author Christian Bauer
 */
public class ProfilePluginModule extends PluginModule implements Comparable {

    private String template;
    private int priority = 100;
    private String[] skins = {"d"};

    public ProfilePluginModule(Plugin plugin, String key) {
        super(plugin, key);
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String[] getSkins() {
        return skins;
    }

    public void setSkins(String[] skins) {
        this.skins = skins;
    }

    public int compareTo(Object o) {
        int result = new Integer(this.getPriority()).compareTo( ((ProfilePluginModule)(o)).getPriority() );
        return result == 0
            ? this.getKey().compareTo( ((ProfilePluginModule)o).getKey() )
            : result;
    }

    // TODO: This is only used in the Administration UI
    public String getModuleTypeLabel() {
        return "Profile";
    }
}
