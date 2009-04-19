/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.jira;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.annotations.*;

import java.util.Collections;
import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("jiraDAO")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class JiraDAO implements Serializable {

    @Logger
    Log log;

    @In("jiraIssueListCache")
    JiraIssueListConnector jiraIssueListConnector;

    public List<JiraIssue> getJiraIssues(String url, String username, String password, String filterId, int numberOfIssues) {

        try {
            List<JiraIssue> issues = jiraIssueListConnector.getIssues(url, username, password, filterId);
            if (issues != null && issues.size() > numberOfIssues) {
                return issues.subList(0, numberOfIssues);
            } else if (issues != null) {
                return issues;
            }
        } catch (IllegalStateException ex) {
            log.warn("jiraIssueListCache was locked by another thread, returning empty list");
        }
        return Collections.EMPTY_LIST;
    }

}
