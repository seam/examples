package org.jboss.seam.security.examples.idmconsole.action;

/**
 * Data transfer object for group information
 *
 * @author Shane Bryzak
 */
public class GroupDTO {
    private String name;
    private String groupType;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupType() {
        return groupType;
    }
}
