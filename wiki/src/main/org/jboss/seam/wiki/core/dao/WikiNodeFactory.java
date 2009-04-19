/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.LinkProtocol;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.Preferences;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Name("wikiNodeFactory")
public class WikiNodeFactory implements Serializable {

    @Logger
    Log log;

    @Observer("Wiki.startup")
    public void checkPreferences() {
        
        log.info("checking wiki preferences...");
/* TODO: needs to be disabled in testing
        // We need a fake user so we can retrieve preferences without a request, on startup
        Contexts.getEventContext().set("currentPreferencesUser", new User());

        Long rootId = loadWikiRoot().getId();
        Long memberId = loadMemberArea().getId();
        Long helpId = loadHelpArea().getId();
        Long trashId = loadTrashArea().getId();

        assert !rootId.equals(memberId);
        assert !rootId.equals(helpId);
        assert !rootId.equals(trashId);
        assert !memberId.equals(helpId);
        assert !memberId.equals(trashId);
        assert !helpId.equals(trashId);
        */
    }

    @Factory(value = "wikiRoot", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadWikiRoot() {
        log.debug("loading wiki root");
        EntityManager em = (EntityManager)Component.getInstance("entityManager");
        try {
            return (WikiDirectory) em
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.parent is null")
                    .setHint("org.hibernate.comment", "Loading wikiRoot")
                    .setHint("org.hibernate.cacheable", false)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

    @Factory(value = "wikiStart", scope = ScopeType.CONVERSATION, autoCreate = true)
    public WikiDocument loadWikiStart() {
        log.debug("loading wiki start into current conversation");
        EntityManager em = (EntityManager)Component.getInstance("restrictedEntityManager");
        try {
            return (WikiDocument) em
                    .createQuery("select d from WikiDocument d where d.id = :id")
                    .setParameter("id", Preferences.instance().get(WikiPreferences.class).getDefaultDocumentId())
                    .setHint("org.hibernate.comment", "Loading wikiStart")
                    .setHint("org.hibernate.cacheable", false)
                    .getSingleResult();
        } catch (EntityNotFoundException ex) {
        } catch (NoResultException ex) {
        }

        // TODO: Message instead!
        throw new RuntimeException("Couldn't find wiki default start document with id '"
                + Preferences.instance().get(WikiPreferences.class).getDefaultDocumentId() +"'");
    }

    // Loads the same instance into a different persistence context
    @Factory(value = "restrictedWikiRoot", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadWikiRootRestricted() {
        log.debug("loading wiki root into restricted PC");
        EntityManager em = (EntityManager)Component.getInstance("restrictedEntityManager");
        WikiDirectory wikiroot = (WikiDirectory) Component.getInstance("wikiRoot");
        try {
            return (WikiDirectory) em
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.id = :id")
                    .setParameter("id", wikiroot.getId())
                    .setHint("org.hibernate.comment", "Loading wikiRootRestricted")
                    .setHint("org.hibernate.cacheable", false)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            throw new RuntimeException("You need to INSERT at least one parentless directory into the database", ex);
        }
    }

    @Factory(value = "memberArea", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadMemberArea() {
        log.debug("loading member area");
        EntityManager em = (EntityManager)Component.getInstance("entityManager");
        String memberAreaName = Preferences.instance().get(WikiPreferences.class).getMemberArea();
        try {
            return (WikiDirectory) em
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.wikiname = :name and d.parent.parent is null")
                    .setParameter("name", WikiUtil.convertToWikiName(memberAreaName) )
                    .setHint("org.hibernate.comment", "Loading memberArea")
                    .setHint("org.hibernate.cacheable", false)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            StatusMessages.instance().addFromResourceBundleOrDefault(
                ERROR,
                "lacewiki.msg.MemberHomedirectoryNotFound",
                "Could not find member area with name {0}  - your configuration is broken, please change it.",
                memberAreaName
            );
            return null;
        }
    }

    @Factory(value = "trashArea", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadTrashArea() {
        log.debug("loading trash area");
        EntityManager em = (EntityManager)Component.getInstance("entityManager");
        String trashAreaName = Preferences.instance().get(WikiPreferences.class).getTrashArea();
        try {
            return (WikiDirectory) em
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.wikiname = :name and d.parent.parent is null")
                    .setParameter("name", WikiUtil.convertToWikiName(trashAreaName) )
                    .setHint("org.hibernate.comment", "Loading trashArea")
                    .setHint("org.hibernate.cacheable", false)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            StatusMessages.instance().addFromResourceBundleOrDefault(
                ERROR,
                "lacewiki.msg.TrashAreaNotFound",
                "Could not find trash area with name {0}  - your configuration is broken, please change it.",
                trashAreaName
            );
            return null;
        }
    }

    @Factory(value = "helpArea", scope = ScopeType.PAGE, autoCreate = true)
    public WikiDirectory loadHelpArea() {
        log.debug("loading help area");
        EntityManager em = (EntityManager)Component.getInstance("entityManager");
        String helpAreaName = Preferences.instance().get(WikiPreferences.class).getHelpArea();
        try {
            return (WikiDirectory) em
                    .createQuery("select d from WikiDirectory d left join fetch d.feed where d.wikiname = :name and d.parent.parent is null")
                    .setParameter("name", WikiUtil.convertToWikiName(helpAreaName) )
                    .setHint("org.hibernate.comment", "Loading trashArea")
                    .setHint("org.hibernate.cacheable", false)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            StatusMessages.instance().addFromResourceBundleOrDefault(
                ERROR,
                "lacewiki.msg.HelpAreaNotFound",
                "Could not find help area with name {0}  - your configuration is broken, please change it.",
                helpAreaName
            );
            return null;
        }
    }

    @Factory(value = "linkProtocolMap", scope = ScopeType.CONVERSATION, autoCreate = true)
    public Map<String, LinkProtocol> loadLinkProtocols() {
        log.debug("loading link protocol map");
        EntityManager em = (EntityManager)Component.getInstance("entityManager");
        Map<String, LinkProtocol> linkProtocols = new TreeMap<String, LinkProtocol>();
        //noinspection unchecked
        List<Object[]> result = em
                .createQuery("select lp.prefix, lp from LinkProtocol lp order by lp.prefix asc")
                .setHint("org.hibernate.comment", "Loading link protocols")
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
        for (Object[] objects : result) {
            linkProtocols.put((String)objects[0], (LinkProtocol)objects[1]);
        }
        return linkProtocols;
    }

}
