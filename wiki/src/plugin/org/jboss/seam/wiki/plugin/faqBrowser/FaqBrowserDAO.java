/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.faqBrowser;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.model.WikiDirectory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("faqBrowserDAO")
@Scope(ScopeType.CONVERSATION)
public class FaqBrowserDAO implements Serializable {

    @In
    EntityManager restrictedEntityManager;

    public WikiDirectory findFaqRootDir(WikiDirectory startDir) {

        StringBuilder queryString = new StringBuilder();
        queryString.append("select dir from WikiDirectory dir join dir.defaultFile f, WikiDocument doc");
        queryString.append(" where dir.nodeInfo.nsThread = :nsThread and");
        queryString.append(" dir.nodeInfo.nsLeft <= :nsLeft and dir.nodeInfo.nsRight >= :nsRight");
        queryString.append(" and f = doc and doc.headerMacrosString like '%faqBrowser%'");

        Query query = restrictedEntityManager.createQuery(queryString.toString())
                .setParameter("nsThread", startDir.getNodeInfo().getNsThread())
                .setParameter("nsLeft", startDir.getNodeInfo().getNsLeft())
                .setParameter("nsRight", startDir.getNodeInfo().getNsRight());

        List<WikiDirectory> result = query.getResultList();

        if (result.size() == 0) return null;

        // We need to iterate through all found directories, these directories are all the "parents" in the
        // tree which might have a default document with "faqBrowser" macro. We assume that the "highest level"
        // directory is the root of the FAQ tree - luckily we have the nested set values to find that one.
        WikiDirectory highestLevelDirectory = result.get(0);
        for (WikiDirectory wikiDirectory : result) {
            if (wikiDirectory.getNodeInfo().getNsLeft() < highestLevelDirectory.getNodeInfo().getNsLeft()) {
                highestLevelDirectory = wikiDirectory;
            }
        }
        return highestLevelDirectory;
    }

}
