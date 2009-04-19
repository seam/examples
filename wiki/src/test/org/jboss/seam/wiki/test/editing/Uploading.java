package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.core.upload.Uploader;
import org.jboss.seam.wiki.core.upload.editor.WikiUploadEditor;
import org.jboss.seam.wiki.core.action.UploadHome;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;
import org.hibernate.StatelessSession;
import org.hibernate.ejb.HibernateEntityManagerFactory;

import java.util.List;

public class Uploading extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/UploadData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test(groups="jdk6-expected-failures")
    public void createUpload() throws Exception {

        final String conversationId = new NonFacesRequest("/uploadCreate_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", "2");
            }
        }.run();

        new FacesRequest("/uploadCreate_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {

                Uploader uploader = (Uploader) getInstance(Uploader.class);

                assert uploader.getParentDirectoryId().equals(2l);

                byte[] uploadData = getBinaryFile("testupload2.zip");
                uploader.setData(uploadData);
                uploader.setContentType("application/zip");
                uploader.setFilename("testupload2.zip");

                assert uploader.uploadNewInstance().equals("WikiUpload");

                assert uploader.getUpload().getContentType().equals("application/zip");
                assert uploader.getUpload().getFilesize() == uploadData.length;
                assert uploader.getUpload().getFilename().equals("testupload2.zip");
                assert uploader.getUpload().getFilenameWithoutExtension().equals("testupload2");
                assert uploader.getUpload().getExtension().equals("zip");
                assert uploader.getUpload().getData() == uploadData;

            }

        }.run();

        new FacesRequest("/uploadEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
                setParameter("parentDirectoryId", "2");
            }

            protected void invokeApplication() throws Exception {

                UploadHome uploadHome = (UploadHome)getInstance(UploadHome.class);
                uploadHome.initEditor();

                assert uploadHome.getInstance().getFilename().equals("testupload2.zip");
                assert uploadHome.getUploadEditor().getClass().equals(WikiUploadEditor.class);
                assert uploadHome.getUploadEditor().getIncludeName().equals("wikiUploadEditor");

                assert invokeMethod("#{uploadHome.persist}").equals("persisted");
            }

            protected void renderResponse() throws Exception {
                StatelessSession ss = getStatelessSession();
                List<String> uploads =
                        ss.createQuery("select wu.filename from WikiUpload wu where wu.parent.id = :parent order by wu.createdOn desc ")
                        .setParameter("parent" , 2l).list();

                assert uploads.size() == 3;
                assert uploads.get(0).equals("testupload2.zip");
            }
        }.run();
    }

    private StatelessSession getStatelessSession() throws Exception {
        org.jboss.ejb3.entity.InjectedEntityManagerFactory jbossEMF =
                (org.jboss.ejb3.entity.InjectedEntityManagerFactory) getInitialContext().lookup("java:/entityManagerFactories/wiki");
        return ((HibernateEntityManagerFactory) jbossEMF.getDelegate()).getSessionFactory().openStatelessSession();
    }


}
