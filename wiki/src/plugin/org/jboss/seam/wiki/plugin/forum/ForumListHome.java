package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;

import javax.persistence.EntityManager;
import java.io.Serializable;

@Name("forumListHome")
@Scope(ScopeType.PAGE)
public class ForumListHome implements Serializable {

    @In
    EntityManager restrictedEntityManager;

    private boolean managed;

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }

    public void manage() {
        managed = true;
    }

}
