/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.jira;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.connectors.jira.JiraDAO;
import org.jboss.seam.wiki.connectors.jira.JiraIssue;
import org.jboss.seam.wiki.core.plugin.WikiPluginMacro;
import org.jboss.seam.wiki.preferences.Preferences;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("jiraIssueList")
@Scope(ScopeType.PAGE)
public class JiraIssueList implements Serializable {

    public static final String MACRO_ATTR_ISSUELIST = "jiraIssueList";

    @In
    JiraDAO jiraDAO;

    public List<JiraIssue> getIssues(WikiPluginMacro macro) {
        List<JiraIssue> issueList = (List<JiraIssue>)macro.getAttributes().get(MACRO_ATTR_ISSUELIST);
        if (issueList == null) {

            JiraIssueListPreferences prefs = Preferences.instance().get(JiraIssueListPreferences.class, macro);

            if (prefs.getUrl() != null && prefs.getUrl().length() > 0
                && prefs.getFilterId() != null && prefs.getFilterId().length() > 0) {

                Integer maxResults = Integer.MAX_VALUE;
                if (prefs.getNumberOfIssues() != null) {
                    maxResults = prefs.getNumberOfIssues().intValue();
                }

                issueList = jiraDAO.getJiraIssues(prefs.getUrl(), prefs.getUsername(), prefs.getPassword(), prefs.getFilterId(), maxResults);
                macro.getAttributes().put(MACRO_ATTR_ISSUELIST, issueList);
            } else {
                macro.getAttributes().put(MACRO_ATTR_ISSUELIST, Collections.EMPTY_LIST);
            }
        }
        return (List<JiraIssue>)macro.getAttributes().get(MACRO_ATTR_ISSUELIST);
    }
    
}
