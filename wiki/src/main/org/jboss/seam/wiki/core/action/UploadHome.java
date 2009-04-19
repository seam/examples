package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.international.Messages;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.model.WikiUpload;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.upload.UploadType;
import org.jboss.seam.wiki.core.upload.UploadTypes;
import org.jboss.seam.wiki.core.upload.Uploader;
import org.jboss.seam.wiki.core.upload.importers.metamodel.ImporterRegistry;
import org.jboss.seam.wiki.core.upload.importers.metamodel.Importer;
import org.jboss.seam.wiki.core.upload.editor.UploadEditor;

import static org.jboss.seam.international.StatusMessage.Severity.INFO;

import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.Collections;

@Name("uploadHome")
@Scope(ScopeType.CONVERSATION)
public class UploadHome extends NodeHome<WikiUpload, WikiDirectory> {

    public static final String UPLOAD_NODE_REMOVER = "uploadNodeRemover";

    /* -------------------------- Context Wiring ------------------------------ */

    @In(required = false)
    Uploader uploader;

    @In
    Map<String, UploadType> uploadTypes;

    @In
    private TagEditor tagEditor;

    @In
    ImporterRegistry importerRegistry;

    /* -------------------------- Internal State ------------------------------ */

    protected UploadEditor uploadEditor;
    protected String importer;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    public Class<WikiUpload> getEntityClass() {
        return WikiUpload.class;
    }

    @Override
    public WikiUpload findInstance() {
        return getWikiNodeDAO().findWikiUpload((Long)getId());
    }

    @Override
    protected WikiDirectory findParentNode(Long parentNodeId) {
        return getEntityManager().find(WikiDirectory.class, parentNodeId);
    }

    @Override
    public WikiUpload afterNodeCreated(WikiUpload ignoredNode) {
        if (uploader == null || uploader.getUpload() == null) {
            throw new RuntimeException("No uploader found for create");
        }
        getLog().debug("initializing with new uploaded file: " + uploader.getFilename());
        WikiUpload upload = uploader.getUpload();
        upload = super.afterNodeCreated(upload);
        initUploadEditor(upload);

        tagEditor.setTags(upload.getTags());

        return upload;
    }

    @Override
    public WikiUpload beforeNodeEditNew(WikiUpload upload) {
        tagEditor.setTags(upload.getTags());
        return super.beforeNodeEditNew(upload);
    }

    @Override
    public WikiUpload afterNodeFound(WikiUpload upload) {
        upload = super.afterNodeFound(upload);

        getLog().debug("initializing with existing upload '" + upload + "' and content type: " + upload.getContentType());

        initUploadEditor(upload);

        tagEditor.setTags(upload.getTags());

        return upload;
    }

    @Override
    public WikiUpload beforeNodeEditFound(WikiUpload upload) {
        tagEditor.setTags(upload.getTags());
        return super.beforeNodeEditFound(upload);
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    protected boolean beforePersist() {
        // Set createdOn date _now_
        getInstance().setCreatedOn(new Date());

        return uploadEditor.beforePersist();
    }

    @Override
    protected boolean beforeUpdate() {
        return uploadEditor.beforeUpdate();
    }

    @Override
    public String remove() {
        return trash();
    }

    @Override
    protected NodeRemover getNodeRemover() {
        return (NodeRemover) Component.getInstance(UploadNodeRemover.class);
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void initUploadEditor(WikiUpload instance) {
        if (uploader != null && uploader.getUpload() != null) {
            uploadEditor = uploader.getUploadHandler().createEditor(uploader.getUpload());
        } else {
            UploadType uploadType = uploadTypes.get(instance.getContentType());
            if (uploadType == null) {
                getLog().debug("couldn't find upload handler for content type, using generic handler and editor");
                uploadType = uploadTypes.get(UploadTypes.GENERIC_UPLOAD_TYPE);
            }
            uploadEditor = uploadType.getUploadHandler().createEditor(instance);
        }
    }

    /* -------------------------- Messages ------------------------------ */

    @Override
    protected void createdMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Upload.Persist",
                "File '{0}' has been saved.",
                getInstance().getName()
        );
    }

    @Override
    protected void updatedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Upload.Update",
                "File '{0}' has been updated.",
                getInstance().getName()
        );
    }

    @Override
    protected void deletedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.Upload.Delete",
                "File '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    protected void uploadUpdatedMessage() {
        StatusMessages.instance().addFromResourceBundleOrDefault(
                INFO,
                "lacewiki.msg.uploadEdit.UpdateUpload",
                "File '{0}' has been uploaded.",
                uploader.getFilename()
        );
    }

    protected String getEditorWorkspaceDescription(boolean create) {
        if (create) {
            return Messages.instance().get("lacewiki.label.uploadEdit.UploadFile");
        } else {
            return Messages.instance().get("lacewiki.label.uploadEdit.EditFile") + ":" + getInstance().getName();
        }
    }

    /* -------------------------- Public Features ------------------------------ */

    public UploadEditor getUploadEditor() {
        if (uploadEditor == null) initUploadEditor(getInstance());
        return uploadEditor;
    }

    public void uploadUpdateInstance() {
        if (uploader.uploadUpdateInstance(getInstance(), true) != null) {
            uploadUpdatedMessage();
        }
    }

    public TagEditor getTagEditor() {
        return tagEditor;
    }

    public List<String> getAvailableImporters() {
        if (getInstance().getContentType() == null) return Collections.EMPTY_LIST;
        return importerRegistry.getAvailableImporters(getInstance().getContentType(), getInstance().getExtension());
    }

    public String getImporter() {
        return importer;
    }

    public void setImporter(String importer) {
        this.importer = importer;
    }

    public void importInstance() {
        if (importer == null) return;

        getLog().debug("importing with importer: " + importer);
        Importer imp = (Importer)Component.getInstance(importer);
        imp.handleImport(getEntityManager(), getInstance());
        getEntityManager().flush();

    }
}
