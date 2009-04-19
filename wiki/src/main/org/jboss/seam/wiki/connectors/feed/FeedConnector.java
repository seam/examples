/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import org.jboss.seam.wiki.core.model.FeedEntry;

import java.util.List;

/**
 * @author Christian Bauer
 */
public interface FeedConnector {

    public List<FeedEntryDTO> getFeedEntries(String feedURL);

}
