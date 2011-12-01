package org.jboss.seam.security.examples.idmconsole.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import org.jboss.seam.security.annotations.permission.PermissionProperty;

import static org.jboss.seam.security.annotations.permission.PermissionPropertyType.*;

/**
 * This entity stores ACL permissions
 *
 * @author Shane Bryzak
 */
@Entity
public class IdentityPermission implements Serializable {
    private static final long serialVersionUID = -5366058398015495583L;

    private Long id;
    private IdentityObject identityObject;
    private IdentityObjectRelationshipType relationshipType;
    private String relationshipName;
    private String resource;
    private String permission;

    /**
     * Surrogate primary key value for the permission.
     *
     * @return
     */
    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Either the specific identity object for which this permission is granted,
     * or in the case of a permission granted against a group, this property
     * then represents the "to" side of the group relationship.  Required field.
     *
     * @return
     */
    @NotNull
    @ManyToOne
    @PermissionProperty(IDENTITY)
    public IdentityObject getIdentityObject() {
        return identityObject;
    }

    public void setIdentityObject(IdentityObject identityObject) {
        this.identityObject = identityObject;
    }

    /**
     * If this permission is granted to a group of identities, then this property may
     * be used to indicate the relationship type of the group membership.  For example,
     * a group or role relationship.  It is possible that the permission may also be
     * granted to identities that have *any* sort of membership within a group, in
     * which case this property would be null.
     *
     * @return
     */
    @ManyToOne
    @PermissionProperty(RELATIONSHIP_TYPE)
    public IdentityObjectRelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(IdentityObjectRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    /**
     * If this permission is granted to a group of identities, then this property
     * may be used to indicate the name for named relationships, such as role
     * memberships.
     *
     * @return
     */
    @PermissionProperty(RELATIONSHIP_NAME)
    public String getRelationshipName() {
        return relationshipName;
    }

    public void setRelationshipName(String relationshipName) {
        this.relationshipName = relationshipName;
    }

    /**
     * The unique identifier for the resource for which permission is granted
     *
     * @return
     */
    @PermissionProperty(RESOURCE)
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     * The permission(s) granted for the resource.  May either be a comma-separated
     * list of permission names (such as create, delete, etc) or a bit-masked
     * integer value, in which each bit represents a different permission.
     *
     * @return
     */
    @PermissionProperty(PERMISSION)
    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
