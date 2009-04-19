/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class BasicNodeOperations extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/HelpDocuments.dbunit.xml", DatabaseOperation.INSERT)
        );
    }


    @Test
    public void editDirectory() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", "2");
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                dirHome.initEditor();

                assert dirHome.getInstance().getId().equals(2l);
                assert dirHome.isHasFeed();
                assert dirHome.getChildDocuments().size() == 1;
                assert dirHome.getMenuItems().size() == 0;
                assert dirHome.getAvailableMenuItems().size() == 0;
            }

        }.run();
    }

    @Test
    public void deleteDirectory() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", "5");
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getInstance().getId().equals(5l); // Init!
                assert invokeMethod("#{directoryHome.remove}").equals("removed");

                // TODO: SeamTest doesn't do navigation but we don't want to have /dirEdit_d.xhtml in the RENDER RESPONSE
                Conversation.instance().end();
                Redirect.instance().setViewId("/dirDisplay.xhtml");
                Redirect.instance().execute();

                assert checkNestedSetNodeInDatabase(1l, 1, 997);
                assert !checkDirectoryInDatabase(5l);
            }

        }.run();
    }

    @Test(groups="jdk6-expected-failures")
    public void createDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                docHome.getInstance().setName("Test Name");
                docHome.getTextEditor().setValue("Test Content");

                assert invokeMethod("#{documentHome.persist}").equals("persisted");

            }

            protected void renderResponse() throws Exception {
                WikiDocument newNode = (WikiDocument)getValue("#{documentHome.instance}");

                assert checkNestedSetNodeInDatabase(3l, 4, 9);
                assert newNode.getAreaNumber().equals(3l);
                assert newNode.getCreatedBy().getId().equals(2l);
                assert newNode.getParent().getId().equals(3l);
                assert newNode.getWikiname().equals("TestName");
                assert newNode.getReadAccessLevel() == 0;
                assert newNode.getWriteAccessLevel() == 0;
                assert newNode.getLastModifiedBy() == null;
                assert newNode.getLastModifiedOn() == null;
                assert newNode.getTags().size() == 0;
                assert checkDocumentInDatabase(newNode.getId());
            }

        }.run();
    }

    @Test
    public void createDocumentTooMuchContent() throws Exception {

        final String conversationId = new NonFacesRequest("/docEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/docEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {

                DocumentHome docHome = (DocumentHome)getInstance(DocumentHome.class);
                docHome.getInstance().setName("Test Name");

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i <= 40000; i++) builder.append("a");

                docHome.getTextEditor().setValue(builder.toString());
                docHome.getTextEditor().validate();
                assert !docHome.getTextEditor().isValid();
            }

        }.run();
    }

    @Test
    public void setDefaultDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", "4");
                setParameter("parentDirectoryId", "3");
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            Long newDefaultDocumentId = null;

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getInstance().getId().equals(4l); // Init!

                // Just take the first one, these should be ordered by name, but there is only one there
                WikiDocument defaultDocument = dirHome.getChildDocuments().get(0);
                dirHome.getInstance().setDefaultFile(defaultDocument);
                newDefaultDocumentId = defaultDocument.getId();

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getInstance().getDefaultFile().getId().equals(newDefaultDocumentId);
            }
        }.run();
    }

    @Test
    public void changeDefaultDocument() throws Exception {

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", "3");
                setParameter("parentDirectoryId", "1");
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            Long newDefaultDocumentId = null;

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();

                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getInstance().getId().equals(3l); // Init!

                // Switch from first to second, these are ordered by name, "One", "Two"
                WikiDocument defaultDocument = dirHome.getChildDocuments().get(1);
                dirHome.getInstance().setDefaultFile(defaultDocument);
                newDefaultDocumentId = defaultDocument.getId();

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getInstance().getDefaultFile().getId().equals(newDefaultDocumentId);
                assert newDefaultDocumentId.equals(7l);
            }
        }.run();
    }

    private boolean checkNestedSetNodeInDatabase(long nodeId, long left, long right) throws Exception {
        Session s = getHibernateSession();
        WikiDirectory dir = (WikiDirectory)s.createQuery("select d from WikiDirectory d  left join fetch d.parent where d.id = :id").setParameter("id", nodeId).uniqueResult();
        s.close();
        return dir.getNodeInfo().getNsLeft() == left && dir.getNodeInfo().getNsRight() == right;
    }

    private boolean checkDirectoryInDatabase(long nodeId) throws Exception {
        Session s = getHibernateSession();
        WikiDirectory dir = (WikiDirectory ) s.createQuery("select d from WikiDirectory d left join fetch d.parent where d.id = :id").setParameter("id", nodeId).uniqueResult();
        s.close();
        return dir != null;
    }

    private boolean checkDocumentInDatabase(long nodeId) throws Exception {
        Session s = getHibernateSession();
        WikiDocument doc = (WikiDocument) s.createQuery("select d from WikiDocument d left join fetch d.parent left join fetch d.tags where d.id = :id").setParameter("id", nodeId).uniqueResult();
        s.close();
        return doc != null;
    }

    private Session getHibernateSession() throws Exception {
        org.jboss.ejb3.entity.InjectedEntityManagerFactory jbossEMF =
                (org.jboss.ejb3.entity.InjectedEntityManagerFactory) getInitialContext().lookup("java:/entityManagerFactories/wiki");
        return ((HibernateEntityManagerFactory) jbossEMF.getDelegate()).getSessionFactory().openSession();
    }

}
