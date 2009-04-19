/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.In;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.cache.PageFragmentCache;

import java.util.List;

/**
 * Invalidates various page fragment caches, listening to core events.
 *
 * @author Christian Bauer
 */
@Name("wikiPageFragmentCacheInvalidator")
public class WikiPageFragmentCacheInvalidator {

    public static final String CACHE_REGION_COMMENT         = "wiki.Comment";
    public static final String CACHE_REGION_SIGNATURE       = "wiki.Signature";

    @In
    PageFragmentCache pageFragmentCache;

    @Observer("User.updated")
    public void invalidateUserSignature(User user) {
        pageFragmentCache.remove(CACHE_REGION_SIGNATURE, user.getId().toString());
    }
}
