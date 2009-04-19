package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.exception.InvalidWikiRequestException;
import org.jboss.seam.wiki.util.Diff;
import org.jboss.seam.wiki.util.WikiUtil;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import static org.jboss.seam.international.StatusMessage.Severity.INFO;

import java.io.Serializable;
import java.util.List;

/**
 * Diff for historical WikiDocuments.
 *
 * TODO: Needs to be generalized to support other WikiFiles, should be easy, except for polymorphic diff() UI, probably
 * hierarchy of WikiFileDiff actions and some page fragments. Maybe move diff() algorithm into each WikiFile subclass.
 *
 * @author Christian Bauer
 */
@Name("documentHistory")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class DocumentHistory implements Serializable {

    private boolean isInitialized = false;

    @Logger
    Log log;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    private StatusMessages statusMessages;

    @DataModel
    private List<WikiFile> historicalFileList;
    public List<WikiFile> getHistoricalFileList() { return historicalFileList; }
    public void setHistoricalFileList(List<WikiFile> historicalFileList) { this.historicalFileList = historicalFileList; }

    @DataModelSelection
    private WikiFile selectedHistoricalFile;
    public WikiFile getSelectedHistoricalFile() { return selectedHistoricalFile; }
    public void setSelectedHistoricalFile(WikiFile selectedHistoricalFile) {
        log.debug("selecting historical file id: " + selectedHistoricalFile.getHistoricalFileId());
        this.selectedHistoricalFile = selectedHistoricalFile;
    }

    Long fileId;
    public Long getFileId() { return fileId; }
    public void setFileId(Long fileId) { this.fileId = fileId; }

    Long historicalFileId;
    public Long getHistoricalFileId() { return historicalFileId; }
    public void setHistoricalFileId(Long historicalFileId) { this.historicalFileId = historicalFileId; }

    private WikiFile currentFile;
    public WikiFile getCurrentFile() { return currentFile;    }

    private WikiFile displayedHistoricalFile;
    public WikiFile getDisplayedHistoricalFile() { return displayedHistoricalFile; }

    private String diffResult;
    public String getDiffResult() { return diffResult; }

    @Factory("historicalFileList")
    public void initializeHistoricalFileList() {
        if (historicalFileList == null) {
            log.debug("initializing list of historical files for file:" + getCurrentFile());
            historicalFileList = wikiNodeDAO.findHistoricalFiles(getCurrentFile());
        }
    }

    public void init() {
        if (!isInitialized) {

            if (getFileId() == null)
                throw new InvalidWikiRequestException("Missing filedId request parameter");

            log.debug("initializing document history with file id: " + getFileId());

            if (currentFile == null) {
                log.debug("loading current file: " + getFileId());
                currentFile = wikiNodeDAO.findWikiDocument(getFileId());

                if (currentFile == null) {
                    throw new org.jboss.seam.framework.EntityNotFoundException(getFileId(), WikiDocument.class);
                }

                if (!Identity.instance().hasPermission("Node", "read", currentFile) ) {
                    throw new AuthorizationException("You don't have permission for this operation");
                }
            }

            initializeHistoricalFileList();
        }


        isInitialized = true;
    }

    public void displayHistoricalRevision() {
        log.debug("displaying historical file id: " + selectedHistoricalFile.getHistoricalFileId());

        displayedHistoricalFile = selectedHistoricalFile;
        diffResult = null;

        statusMessages.addFromResourceBundleOrDefault(
            INFO,
            "lacewiki.msg.DiffOldVersionDisplayed",
            "Showing historical revision {0}",
            selectedHistoricalFile.getRevision()
        );
    }

    public void diffHistoricalRevision() {
        log.debug("diffing historical file id: " + selectedHistoricalFile.getHistoricalFileId());

        String[] a = ((WikiDocument)selectedHistoricalFile).getContent().split("\n");
        String[] b = ((WikiDocument)currentFile).getContent().split("\n");

        StringBuilder result = new StringBuilder();
        List<Diff.Difference> differences = new Diff(a, b).diff();

        // TODO: Externalize and i18n these strings
        for (Diff.Difference diff : differences) {
            int        delStart = diff.getDeletedStart();
            int        delEnd   = diff.getDeletedEnd();
            int        addStart = diff.getAddedStart();
            int        addEnd   = diff.getAddedEnd();
            String     type     = delEnd != Diff.NONE && addEnd != Diff.NONE ? "changed" : (delEnd == Diff.NONE ? "added" : "deleted");

            // Info line
            result.append("<div class=\"diffInfo\">");
            result.append("From ");
            result.append(delStart == delEnd || delEnd == Diff.NONE ? "line" : "lines");
            result.append(" ");
            result.append(delStart);
            if (delEnd != Diff.NONE && delStart != delEnd) {
                result.append(" to ").append(delEnd);
            }
            result.append(" ").append(type).append(" to ");
            result.append(addStart == addEnd || addEnd == Diff.NONE ? "line" : "lines");
            result.append(" ");
            result.append(addStart);
            if (addEnd != Diff.NONE && addStart != addEnd) {
                result.append(" to ").append(addEnd);
            }
            result.append(":");
            result.append("</div>\n");

            if (delEnd != Diff.NONE) {
                result.append("<div class=\"diffDeleted\">");
                for (int lnum = delStart; lnum <= delEnd; ++lnum) {
                    result.append( WikiUtil.escapeHtml(a[lnum], false, false) ).append("<br/>");
                }
                result.append("</div>");
                if (addEnd != Diff.NONE) {
                    //result.append("----------------------------").append("\n");
                }
            }
            if (addEnd != Diff.NONE) {
                result.append("<div class=\"diffAdded\">");
                for (int lnum = addStart; lnum <= addEnd; ++lnum) {
                    result.append( WikiUtil.escapeHtml(b[lnum], false, false) ).append("<br/>");
                }
                result.append("</div>");
            }
        }

        diffResult = result.toString();

        statusMessages.addFromResourceBundleOrDefault(
            INFO,
            "lacewiki.msg.DiffCreated",
            "Comparing current revision with historical revision {0}",
            selectedHistoricalFile.getRevision()
        );
    }

    // This methods takes the historicalFileId parameter to load a revision from the DB
    public void diff() {
        init(); // TODO: Why doesn't Seam execute my page action but instead s:link action="diff" in a fake RENDER RESPONSE?!?
        displayedHistoricalFile = null;

        if (historicalFileId == null) return;
        selectedHistoricalFile = wikiNodeDAO.findHistoricalDocumentAndDetach(getCurrentFile().getHistoricalEntityName(), historicalFileId);
        if (selectedHistoricalFile == null) {
            statusMessages.addFromResourceBundleOrDefault(
                ERROR,
                "lacewiki.msg.HistoricalNodeNotFound",
                "Couldn't find historical node: {0}",
                historicalFileId
            );
            return;
        }
        diffHistoricalRevision();
    }

    @Restrict("#{s:hasPermission('Node', 'edit', documentHistory.currentFile)}")
    public String rollback() {
        statusMessages.addFromResourceBundleOrDefault(
            INFO,
            "lacewiki.msg.RollingBackDocument",
            "Rolling document back to revision {0}",
            selectedHistoricalFile.getRevision()
        );
        return "rollback";
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public String purgeHistory() {
        wikiNodeDAO.removeHistoricalFiles(getCurrentFile());
        return "purgedHistory";
    }

}
