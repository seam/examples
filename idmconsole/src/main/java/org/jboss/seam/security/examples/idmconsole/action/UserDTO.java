package org.jboss.seam.security.examples.idmconsole.action;

/**
 * Used to transfer user information to a view layer
 *
 * @author Shane Bryzak
 */
public class UserDTO {
    private String username;
    private boolean enabled;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
