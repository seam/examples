package org.jboss.seam.security.examples.authorization;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.annotations.LoggedIn;
import org.jboss.seam.security.examples.authorization.annotations.Admin;
import org.jboss.seam.security.examples.authorization.annotations.Foo;
import org.jboss.seam.security.examples.authorization.annotations.User;

/**
 * @author Shane Bryzak
 */
public
@Model
class PrivilegedActions {
    @Inject Messages messages;

    @Admin
    public void doSomethingRestricted() {
        messages.info("doSomethingRestricted() invoked");
    }

    @Foo(bar = "abc", zzz = "nonbindingvalue")
    public void doFooAbc() {
        messages.info("doFooAbc() invoked");
    }

    @Foo(bar = "def")
    public void doFooDef() {
        messages.info("doFooDef() invoked");
    }

    @LoggedIn
    public void doLoggedIn() {
        messages.info("doLoggedIn() invoked");
    }

    @User
    public void doUserAction() {
        messages.info("doUserAction() invoked");
    }
    
    @Foo(bar = "demo")
    public void doDemoUserRuleAction() {
        messages.info("doDemoUserRuleAction() invoked");
    }
    
    @Foo(bar = "user")
    public void doInUserGroupRuleAction() {
        messages.info("doInUserGroupRuleAction() invoked");
    }
}
