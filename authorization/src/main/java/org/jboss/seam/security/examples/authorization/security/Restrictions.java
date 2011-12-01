package org.jboss.seam.security.examples.authorization.security;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;
import org.jboss.seam.security.examples.authorization.annotations.Admin;
import org.jboss.seam.security.examples.authorization.annotations.Foo;
import org.jboss.seam.security.examples.authorization.annotations.User;

/**
 * @author Shane Bryzak
 */
public class Restrictions {
    public
    @Secures
    @Admin
    boolean isAdmin(Identity identity) {
        return identity.hasRole("admin", "USERS", "GROUP");
    }

    public
    @Secures
    @Foo(bar = "abc")
    boolean isFooAbc() {
        return true;
    }

    public
    @Secures
    @Foo(bar = "def")
    boolean isFooDef() {
        return false;
    }

    public
    @Secures
    @User
    boolean isUser(Identity identity) {
        return identity.inGroup("USERS", "GROUP");
    }
    
    public @Secures @Foo(bar = "demo") boolean isDemoUser(Identity identity) {
        return identity.hasPermission("foo", "execute");
    }
    
    public @Secures @Foo(bar = "user") boolean isInUserGroup(Identity identity) {
        return identity.hasPermission("bar", "execute");
    }
    
}
