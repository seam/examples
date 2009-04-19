/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.connector;

import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.connectors.jira.JiraDAO;
import org.jboss.seam.wiki.connectors.jira.JiraIssueListConnector;
import org.jboss.seam.wiki.connectors.jira.JiraIssue;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.ScopeType;
import org.dbunit.operation.DatabaseOperation;
import org.testng.annotations.Test;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Christian Bauer
 */
public class JiraConnectorTest extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }

    @Test
    public void cacheIssueList() throws Exception {

        new NonFacesRequest() {
            protected void renderResponse() throws Exception {
                JiraDAO dao = (JiraDAO)getInstance(JiraDAO.class);

                List<JiraIssue> issues = dao.getJiraIssues("foo", "bar", "baz", "hum", 3);
                assert issues.size() == 0; // Asynchronous cache needs to do its job first

                Thread.sleep(1000);

                issues = dao.getJiraIssues("foo", "bar", "baz", "hum", 3);
                assert issues.size() == 0; // Still nothing

                Thread.sleep(3000);

                issues = dao.getJiraIssues("foo", "bar", "baz", "hum", 3);
                assert issues.size() == 3; // Now we have it
            }
        }.run();
    }

    @Name("jiraIssueListConnector")
    @Scope(ScopeType.APPLICATION)
    @Install(precedence = Install.MOCK)
    @AutoCreate
    public static class MockJiraIssueListConnector implements JiraIssueListConnector {
        public List<JiraIssue> getIssues(String url, String username, String password, String filterId) {

            try {
                Thread.sleep(3000); // Wait 3 seconds...
            } catch (InterruptedException e) {}

            List<JiraIssue> issues = new ArrayList<JiraIssue>();

            JiraIssue i1 = new JiraIssue();
            i1.setKey("one");
            issues.add(i1);

            JiraIssue i2 = new JiraIssue();
            i1.setKey("two");
            issues.add(i2);

            JiraIssue i3 = new JiraIssue();
            i1.setKey("three");
            issues.add(i3);

            JiraIssue i4 = new JiraIssue();
            i1.setKey("four");
            issues.add(i4);

            return issues;
        }
    }

}
