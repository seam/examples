/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.jira;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Single-threaded component used for accessing a remote JIRA webservice.
 * <p>
 * Do NOT access this component without synchronizing multi-thread access!
 * </p>
 * http://confluence.atlassian.com/pages/viewpage.action?pageId=1035
 * http://docs.atlassian.com/software/jira/docs/api/rpc-jira-plugin/latest/com/atlassian/jira/rpc/xmlrpc/XmlRpcService.html
 * http://ws.apache.org/xmlrpc/apidocs/index.html?index-all.html
 *
 * @author Christian Bauer
 */
@Name("jiraConnector")
@Roles(
    @Role(name = "jiraIssueListConnector")
)
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class JiraConnector implements JiraIssueListConnector {

    private static final String RPC_PATH = "/rpc/xmlrpc";

    @Logger
    Log log;

    @In("#{preferences.get('JiraConnector')}")
    JiraConnectorPreferences prefs;

    XmlRpcClient client = new XmlRpcClient();

    private String connectAndLogin(String url, String username, String password) {
        if (url.endsWith("/")) url = url.substring(0, url.length()-1);

        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(url + RPC_PATH));
            config.setConnectionTimeout(prefs.getConnectionTimeoutSeconds().intValue());
            config.setReplyTimeout(prefs.getReplyTimeoutSeconds().intValue());

            client = new XmlRpcClient();
            client.setConfig(config);

            List loginParams = new ArrayList(2);
            loginParams.add(username);
            loginParams.add(password);
            return (String) client.execute("jira1.login", loginParams);

        } catch (MalformedURLException urlEx) {
            log.warn("URL is not valid: " + url);
            return null;
        } catch (Exception ex) {
            log.warn("couldn't connect to remote JIRA webservice: " + url + RPC_PATH + ", " + ex.getMessage());
            return null;
        }
    }

    private boolean disconnectAndLogout(String loginToken) {
        try {
            return (Boolean) client.execute("jira1.logout", wrapLoginToken(loginToken));
        } catch (XmlRpcException rpcEx) {
            log.warn("couldn't disconnect from remote JIRA webservice, " + rpcEx.getMessage());
        }
        return false;
    }

    private List wrapLoginToken(String loginToken) {
        List loginTokenParam = new ArrayList(1);
        loginTokenParam.add(loginToken);
        return loginTokenParam;
    }

    public List<JiraIssue> getIssues(String url, String username, String password, String filterId) {
        try {
            log.debug("getting issue list from remote JIRA webservice: " + url + " using filter: " + filterId);
            String loginToken = connectAndLogin(url, username, password);
            if (loginToken != null) {
                log.debug("remote login successful, retrieving list");
                List<String> params = new ArrayList<String>(2);
                params.add(loginToken);
                params.add(filterId);
                Object[] result = (Object[]) client.execute("jira1.getIssuesFromFilter", params);

                List<JiraIssue> issues = new ArrayList<JiraIssue>();
                for (Object r : result) {
                    JiraIssue issue = JiraIssue.fromMap((Map)r);
                    log.trace("retrieved remote object: " + issue);
                    issues.add(issue);
                }

                disconnectAndLogout(loginToken);

                return issues;
            }
        } catch (Exception ex) {
            log.warn("exception while executing remote JIRA webservice operation", ex);
            return Collections.emptyList();
        }
        return Collections.EMPTY_LIST;
    }
}
