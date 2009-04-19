/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.jira;

import java.util.List;

/**
 * @author Christian Bauer
 */
public interface JiraIssueListConnector {

    public List<JiraIssue> getIssues(String url, String username, String password, String filterId);

}
