/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.jira;

import org.jboss.seam.wiki.connectors.cache.ConnectorCache;
import org.jboss.seam.wiki.connectors.cache.ConnectorCacheKey;
import org.jboss.seam.wiki.connectors.cache.ConnectorCacheAsyncUpdater;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.async.Asynchronous;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("jiraIssueListCache")
@AutoCreate
public class JiraIssueListCache
        extends ConnectorCache<JiraIssue, JiraIssueListCache.JiraIssueListCacheKey>
        implements JiraIssueListConnector {

    @In("#{preferences.get('JiraConnector')}")
    JiraConnectorPreferences prefs;

    public List<JiraIssue> getIssues(String url, String username, String password, String filterId) {
        JiraIssueListCacheKey newKey = new JiraIssueListCacheKey(url, username, password, filterId);
        return lookup( new ConnectorCacheKey<JiraIssueListCacheKey>(newKey));
    }

    protected long getUpdateTimeoutSeconds() {
        return prefs.getIssueListCacheUpdateTimeoutSeconds();
    }

    protected long getIdleTimeoutSeconds() {
        return prefs.getIssueListCacheIdleTimeoutSeconds();
    }

    protected Class<? extends ConnectorCacheAsyncUpdater<JiraIssue, JiraIssueListCacheKey>> getAsyncUpdaterClass() {
        return JiraIssueListCacheAsyncUpdater.class;
    }

    @Name("jiraIssueListCacheAsyncUpdater")
    @AutoCreate
    public static class JiraIssueListCacheAsyncUpdater
            extends ConnectorCacheAsyncUpdater<JiraIssue, JiraIssueListCacheKey> {

        @In
        JiraIssueListConnector jiraIssueListConnector;

        @Asynchronous
        public void updateCacheAsynchronously(ConnectorCache<JiraIssue, JiraIssueListCacheKey> cache,
                                              ConnectorCacheKey<JiraIssueListCacheKey> key) {
            List<JiraIssue> result =
                jiraIssueListConnector.getIssues(
                    key.getKeyValue().getUrl(),
                    key.getKeyValue().getUsername(),
                    key.getKeyValue().getPassword(),
                    key.getKeyValue().getFilterId()
                );
            if (result.size() >0)
                super.writeIntoCache(cache, key, result);
        }

    }

    public static class JiraIssueListCacheKey implements Serializable {
        private String url;
        private String username;
        private String password;
        private String filterId;

        public JiraIssueListCacheKey(String url, String username, String password, String filterId) {
            this.url = url;
            this.username = username;
            this.password = password;
            this.filterId = filterId;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getFilterId() {
            return filterId;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            JiraIssueListCacheKey  that = (JiraIssueListCacheKey ) o;

            if (!filterId.equals(that.filterId)) return false;
            if (!password.equals(that.password)) return false;
            if (!url.equals(that.url)) return false;
            if (!username.equals(that.username)) return false;

            return true;
        }

        public int hashCode() {
            int result;
            result = url.hashCode();
            result = 31 * result + username.hashCode();
            result = 31 * result + password.hashCode();
            result = 31 * result + filterId.hashCode();
            return result;
        }
    }

}
