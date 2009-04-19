/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.web.Parameters;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.search.WikiSearch;

import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.international.StatusMessage;

/**
 * Returns <tt>docDisplay</tt>, <tt>dirDisplay</tt>, or <tt>search</tt> for the resolved <tt>nodeId</tt>.
 * <p>
 * This resolver expects request parameters in the following format:
 * </p>
 * <pre>
 * http://host/         -- rewrite filter --> http://host/context/wiki.seam
 * http://host/123.html -- rewrite filter --> http://host/context/wiki.seam?nodeId=123
 * http://host/Foo      -- rewrite filter --> http://host/context/wiki.seam?areaName=Foo
 * http://host/Foo/Bar  -- rewrite filter --> http://host/context/wiki.seam?areaName=Foo&nodeName=Bar
 * </pre>
 * <p>
 * 'Foo' is a WikiName of a directory with a parentless parent (ROOT), we call this a logical area.
 * 'Bar' is a WikiName of a node in that logical area, unique within that area subtree. A node can either
 * be a document or a directory, so we don't know what 'Bar' is until we searched for it by its unique
 * name inside the area.
 * </p>
 * <p>
 * We _never_ have URLs like <tt>http://host/Foo/Baz/Bar</tt> because 'Baz' would be a subdirectory
 * we don't need. An area name and a node name is enough, the node name is unique within
 * a subtree. We also never have <tt>http://host/Bar</tt>, a node name alone is not enough to
 * identify a node, we also need the area name. Of course, <tt>http://host/Foo</tt> is enough, then
 * we look for a default document of that area.
 * </p>
 *<p>
 * If the given parameters can't be resolved, the following prodecure applies:
 * </p>
 * <ul>
 * <li> A fulltext search with the supplied area name is attempted, e.g. the request
 *      <tt>http://host/context/wiki.seam?areaName=HelpMe</tt> will result int a wiki fulltext
 *      search for the string "HelpMe"
 * </li>
 * <li>
 *      If the fulltext search did not return any results, the <tt>wikiStart</tt> node is displayed, as
 *      defined in the wiki preferences.
 * </li>
 * </ul>
 * <p>
 * Note that this resolver also sets the identifier and instance on the respetive *Home, e.g. on
 * <tt>documentHome</tt> when <tt>docDisplay</tt> is returned.
 * </p>
 *
 * @author Christian Bauer
 */
@Name("wikiRequestResolver")
@Scope(ScopeType.EVENT)
@AutoCreate
public class WikiRequestResolver {

    public static final String SESSION_MSG = "lacewiki.Session.Message";
    public static final String SESSION_MSG_SEVERITY = "lacewiki.Session.MessageSeverity";
    public static final String SESSION_MSG_DATA = "lacewiki.Session.MessageData";

    @Logger
    static Log log;

    @In
    protected WikiNodeDAO wikiNodeDAO;

    protected Long nodeId;

    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
    
    protected String areaName;
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName != null && areaName.length() > 0 ? areaName : null; }

    protected String nodeName;
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName != null && nodeName.length() > 0 ? nodeName : null; }

    protected String messageKey;
    protected String messageSeverity;

    public String getMessageKey() { return messageKey; }
    public void setMessageKey(String messageKey) { this.messageKey = messageKey; }

    public String getMessageSeverity() { return messageSeverity; }
    public void setMessageSeverity(String messageSeverity) { this.messageSeverity = messageSeverity;}

    protected WikiDocument currentDocument = null;
    protected WikiDirectory currentDirectory = null;

    public String resolve() {
        log.debug("resolving wiki request, node id: " + getNodeId() + " area name: " + getAreaName() + " node name: " + getNodeName());

        // Push optional request parameters into contexts
        resolveRequestParameters();

        // Queue a message if requested (for message passing across session invalidations and conversations)
        if (getMessageKey() != null) {
            log.debug("wiki request contained message: " + getMessageKey());
            StatusMessage.Severity msgSeverity = StatusMessage.Severity.INFO;
            if (getMessageSeverity() != null && getMessageSeverity().length() > 0) {
                try {
                    msgSeverity = StatusMessage.Severity.valueOf(getMessageSeverity().trim());
                } catch (IllegalArgumentException ex) {
                    // Swallow
                }
            }
            StatusMessages.instance().addFromResourceBundle(msgSeverity, getMessageKey());
        }

        // Queue a message if requested in the session (for message passing across conversations)
        String msgKey = (String)Contexts.getSessionContext().get(SESSION_MSG);
        if (msgKey != null) {
            log.debug("session contained message: " + msgKey);

            StatusMessage.Severity msgSeverity = StatusMessage.Severity.INFO;
            StatusMessage.Severity sessionMessageSeverity =
                    (StatusMessage.Severity)Contexts.getSessionContext().get(SESSION_MSG_SEVERITY);
            if (sessionMessageSeverity != null) {
                msgSeverity = sessionMessageSeverity;
            }
            Object msgData = Contexts.getSessionContext().get(SESSION_MSG_DATA);
            if (msgData != null) {
                StatusMessages.instance().addFromResourceBundle(msgSeverity, msgKey, msgData);
            } else {
                StatusMessages.instance().addFromResourceBundle(msgSeverity, msgKey);
            }
            Contexts.getSessionContext().remove(SESSION_MSG);
            Contexts.getSessionContext().remove(SESSION_MSG_SEVERITY);
            Contexts.getSessionContext().remove(SESSION_MSG_DATA);
        }

        // Have we been called with a nodeId request parameter, must be a document
        if (nodeId != null) {
            log.debug("trying to resolve node id: " + nodeId);

            // Try to find a document
            currentDocument = wikiNodeDAO.findWikiDocument(nodeId);
            if (currentDocument != null) {
                // Document found, take its directory
                // TODO: Avoid cast
                currentDirectory = (WikiDirectory)currentDocument.getParent();
            } else {
                // Let's check if the id was a directory
                currentDirectory = wikiNodeDAO.findWikiDirectory(nodeId);

            }

        // Have we been called with an areaName and nodeName request parameter
        } else if (areaName != null && nodeName != null) {
            log.debug("trying to resolve area name: " + areaName + " and node name: " + nodeName);

            // Try to find the area/directory
            WikiDirectory area = wikiNodeDAO.findArea(areaName);
            if (area != null) {

                // Try to find the document
                WikiDocument doc = wikiNodeDAO.findWikiDocumentInArea(area.getAreaNumber(), nodeName);
                if (doc != null) {
                    // Found it, let's use that
                    currentDocument = doc;
                    // TODO: Avoid cast
                    currentDirectory = (WikiDirectory)currentDocument.getParent();
                } else {
                    // Didn't find a document for the node name, let's see if it's a directory
                    currentDirectory = wikiNodeDAO.findWikiDirectoryInArea(area.getAreaNumber(), nodeName);
                }
            }

        // Or have we been called just with an areaName request parameter
        } else if (areaName != null) {
            log.debug("trying to resolve area name: " + areaName);
            currentDirectory = wikiNodeDAO.findArea(areaName);
        }

        log.debug("resolved directory: " + currentDirectory + " and document: " + currentDocument);

        // Fall back, take the area name as a search query
        if (currentDirectory == null) {
            boolean foundMatches = false;
            if (areaName != null && areaName.length() > 0) {
                log.debug("searching for unknown area name: " + areaName);
                WikiSearch wikiSearch = (WikiSearch) Component.getInstance(WikiSearch.class);
                wikiSearch.setSimpleQuery(areaName);
                wikiSearch.search();
                foundMatches = wikiSearch.getTotalCount() > 0;
            }
            if (foundMatches) {
                log.debug("found search results");
                return "search";
            } else {
                log.debug("falling back to wiki start document");
                // Fall back to default document
                currentDocument = (WikiDocument)Component.getInstance("wikiStart");
                // TODO: Avoid cast
                currentDirectory = (WikiDirectory)currentDocument.getParent();
            }
        }

        // Last attempt, in case nothing worked try the default document if we have a directory
        if (currentDirectory != null && currentDocument == null) {
            // We have a directory, let's see if it has a default file and if that is a document we can use
            // TODO: Default can be a file, not only a document, currently the UI only allows you to set documents,
            // so narrow the Hibernate proxy down to a document with a special DAO method and a NO_PROXY mapping
            currentDocument = wikiNodeDAO.findDefaultDocument(currentDirectory);
        }

        if (currentDocument != null) {
            DocumentHome documentHome = (DocumentHome)Component.getInstance(DocumentHome.class);
            documentHome.setNodeId(currentDocument.getId());
            documentHome.setInstance(currentDocument);
            documentHome.afterNodeFound(currentDocument);
            log.debug("displaying document: " + currentDocument);
            return "docDisplay";
        } else {
            DirectoryBrowser directoryBrowser = (DirectoryBrowser)Component.getInstance(DirectoryBrowser.class);
            directoryBrowser.setDirectoryId(currentDirectory.getId());
            directoryBrowser.setInstance(currentDirectory);
            directoryBrowser.afterNodeFound();
            log.debug("displaying directory: " + currentDirectory);
            return "dirDisplay";
        }
    }

    // These are pushed into the EVENT context, if present in the request (used by plugins etc.)
    public static enum OptionalParameter {

        category("requestedCategory", String.class),
        year("requestedYear", Long.class),
        month("requestedMonth", Long.class),
        day("requestedDay", Long.class),
        page("requestedPage", Long.class);

        String variableName;
        Class variableType;
        public String getVariableName() {
            return variableName;
        }
        public Class getVariableType() {
            return variableType;
        }
        OptionalParameter(String variableName, Class variableType) {
            this.variableName = variableName;
            this.variableType = variableType;
        }
    }

    private void resolveRequestParameters() {
        log.debug("resolving (optional) request paramters");

        OptionalParameter[] optionalParams = OptionalParameter.values();
        for (OptionalParameter optionalParam : optionalParams) {
            Object value =
                Parameters.instance().convertMultiValueRequestParameter(
                    Parameters.instance().getRequestParameters(), optionalParam.name(), optionalParam.getVariableType()
                );
            if (value != null) {
                log.debug("found request parameter '" + optionalParam.name() + "', setting '"
                           + optionalParam.getVariableName()+"' in EVENT context: " + value);
                Contexts.getEventContext().set(optionalParam.variableName, value);
            }
        }
    }

}
