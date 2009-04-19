/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.hibernate.validator.Length;
import org.hibernate.validator.Email;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "USERS")
@org.hibernate.annotations.BatchSize(size = 20)
public class User implements Serializable {

    public static final String GUEST_USERNAME = "guest";
    public static final String ADMIN_USERNAME = "admin";

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "USER_ID")
    private Long id = null;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    private int version = 0;

    @Column(name = "FIRSTNAME", length = 63, nullable = false)
    @NotNull
    @Length(min = 1, max = 63)
    @Pattern(
        regex="[^\\t\\n\\r\\f\\a\\e]+",
        message="#{messages['lacewiki.entity.NameMustNotContainSpecialCharacters']}"
    )
    private String firstname;

    @Column(name = "LASTNAME", length = 63, nullable = false)
    @NotNull
    @Length(min = 1, max = 63)
    @Pattern(
        regex="[^\\t\\n\\r\\f\\a\\e]+",
        message="#{messages['lacewiki.entity.NameMustNotContainSpecialCharacters']}"
    )
    private String lastname;

    @Column(name = "USERNAME", length = 16, nullable = false, unique = true)
    @NotNull
    @Length(min = 1, max = 16)
    @Pattern(
        regex="[a-zA-Z]?[a-zA-Z0-9]+",
        message="#{messages['lacewiki.entity.UsernameMustStartWithALetterAndOnlyContainLetters']}"
    )
    private String username; // Unique and immutable

    @Column(name = "PASSWORDHASH", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "EMAIL", length = 255, nullable = false)
    @NotNull
    @Email
    private String email;

    @Column(name = "ACTIVATED", nullable = false)
    private boolean activated = false;

    @Column(name = "ACTIVATION_CODE", length = 255, nullable = true)
    private String activationCode;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();

    @Column(name = "LAST_LOGIN_ON", nullable = true)
    private Date lastLoginOn = new Date();

    @Transient
    private Date previousLastLoginOn = new Date();

    @ManyToMany(fetch = FetchType.LAZY) // Lazy so our @OrderBy works
    @JoinTable(
        name = "USER_ROLE",
        joinColumns = @JoinColumn(name = "USER_ID"),
        inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    @OrderBy("accessLevel desc, displayName asc")
    @org.hibernate.annotations.ForeignKey(name = "USER_ROLE_USER_ID", inverseName = "USER_ROLE_ROLE_ID")
    private List<Role> roles = new ArrayList<Role>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_HOME_WIKI_DIRECTORY_ID", nullable = true, unique = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_USER_MEMBER_HOME_WIKI_DIRECTORY_ID")
    private WikiDirectory memberHome;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "USER_PROFILE_ID", nullable = false, unique = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_USER_USER_PROFILE_ID")
    private UserProfile profile = new UserProfile();

    @Transient
    private long ratingPoints = 0;

    public User() {}

    public User(String firstname, String lastname,
                String username, String passwordHash, String email) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    // Immutable properties

    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }

    // Mutable properties

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getFullname() {
        return getFirstname() + " " + getLastname();
    }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActivated() { return activated; }
    public void setActivated(boolean activated) { this.activated = activated; }

    public Date getLastLoginOn() { return lastLoginOn; }
    public void setLastLoginOn(Date lastLoginOn) { this.lastLoginOn = lastLoginOn; }

    public Date getPreviousLastLoginOn() { return previousLastLoginOn; }
    public void setPreviousLastLoginOn(Date previousLastLoginOn) { this.previousLastLoginOn = previousLastLoginOn; }

    public String getActivationCode() { return activationCode; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }

    public WikiDirectory getMemberHome() { return memberHome; }
    public void setMemberHome(WikiDirectory memberHome) { this.memberHome = memberHome; }

    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }

    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }

    public long getRatingPoints() { return ratingPoints; }
    public void setRatingPoints(long ratingPoints) { this.ratingPoints = ratingPoints; }

    // Misc methods

    public boolean isGuest() {
        return GUEST_USERNAME.equals(getUsername());
    }

    public boolean isAdmin() {
        return ADMIN_USERNAME.equals(getUsername());
    }

    public String toString() {
        return  getUsername();
    }
    
}


