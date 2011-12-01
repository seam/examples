package org.jboss.seam.security.examples.idmconsole.model;

import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.Initialized;
import org.jboss.seam.transaction.Transactional;

/**
 * Loads initial data in a platform-independent way
 *
 * @author Marek Schmidt
 */
public class PopulateDatabase {
    
    @PersistenceContext
    EntityManager entityManager;
    
    @Transactional
    public void loadData(@Observes @Initialized WebApplication webapp) {
        // Roles
        IdentityRoleName admin = new IdentityRoleName();
        admin.setName("admin");
        entityManager.persist(admin);
        
        IdentityRoleName manager = new IdentityRoleName();
        manager.setName("manager");
        entityManager.persist(manager);
        
        // Object types
        IdentityObjectType USER = new IdentityObjectType();
        USER.setName("USER");
        entityManager.persist(USER);
        
        IdentityObjectType GROUP = new IdentityObjectType();
        GROUP.setName("GROUP");
        entityManager.persist(GROUP);
        
        // Objects
        IdentityObject user = new IdentityObject();
        user.setName("shane");
        user.setType(USER);
        entityManager.persist(user);
        
        IdentityObject demo = new IdentityObject();
        demo.setName("demo");
        demo.setType(USER);
        entityManager.persist(demo);
        
        IdentityObject headOffice = new IdentityObject();
        headOffice.setName("Head Office");
        headOffice.setType(GROUP);
        entityManager.persist(headOffice);
        
        IdentityObject foo = new IdentityObject();
        foo.setName("foo");
        foo.setType(USER);
        entityManager.persist(foo);
        
        // Credential types
        IdentityObjectCredentialType PASSWORD = new IdentityObjectCredentialType();
        PASSWORD.setName("PASSWORD");
        entityManager.persist(PASSWORD);
        
        // Credentials
        IdentityObjectCredential userPassword = new IdentityObjectCredential();
        userPassword.setIdentityObject(user);
        userPassword.setType(PASSWORD);
        userPassword.setValue("password");
        entityManager.persist(userPassword);
        
        IdentityObjectCredential demoPassword = new IdentityObjectCredential();
        demoPassword.setIdentityObject(demo);
        demoPassword.setType(PASSWORD);
        demoPassword.setValue("demo");
        entityManager.persist(demoPassword);
        
        // Object relationship types
        IdentityObjectRelationshipType jbossIdentityMembership = new IdentityObjectRelationshipType();
        jbossIdentityMembership.setName("JBOSS_IDENTITY_MEMBERSHIP");
        entityManager.persist(jbossIdentityMembership);
        
        IdentityObjectRelationshipType jbossIdentityRole = new IdentityObjectRelationshipType();
        jbossIdentityRole.setName("JBOSS_IDENTITY_ROLE");
        entityManager.persist(jbossIdentityRole);
        
        // Object relationships
        IdentityObjectRelationship demoAdminRole = new IdentityObjectRelationship();
        demoAdminRole.setName("admin");
        demoAdminRole.setRelationshipType(jbossIdentityRole);
        demoAdminRole.setFrom(headOffice);
        demoAdminRole.setTo(demo);
        entityManager.persist(demoAdminRole);
    }
}
