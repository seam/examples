/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.editing;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiMenuItem;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class EditMenu extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
                new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
    }


    @Test
    public void checkAvailableMenuItems() throws Exception {

        loginAdmin();

        final String conversationId = new NonFacesRequest("/dirEdit_d.xhtml") {
            protected void beforeRequest() {
                setParameter("directoryId", "2");
                setParameter("parentDirectoryId", "1");
            }
        }.run();

        new FacesRequest("/dirEdit_d.xhtml") {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void invokeApplication() throws Exception {
                assert Conversation.instance().isLongRunning();
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getInstance().getId().equals(2l); // Init!
                assert dirHome.getAvailableMenuItems().size() == 0;
            }
        }.run();
    }

    @Test
    public void addMenuItem() throws Exception {

        loginAdmin();

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

                assert dirHome.getMenuItems().size() == 1;

                assert dirHome.getAvailableMenuItems().size() == 1;

                WikiDirectory newMenuItem = dirHome.getAvailableMenuItems().iterator().next();
                dirHome.setSelectedChildDirectory(newMenuItem);
                dirHome.addMenuItem();

                assert dirHome.getMenuItems().size() == 2;

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getMenuItems().size() == 2;
                WikiMenuItem[] menuItems = dirHome.getMenuItems().toArray(new WikiMenuItem[dirHome.getMenuItems().size()]);
                assert menuItems[0].getDirectoryId().equals(4l);
                assert menuItems[0].getDirectory().getId().equals(4l);
                assert menuItems[0].getDisplayPosition() == 0;
                assert menuItems[1].getDirectoryId().equals(5l);
                assert menuItems[1].getDirectory().getId().equals(5l);
                assert menuItems[1].getDisplayPosition() == 1;
            }
        }.run();
    }

    @Test
    public void removeMenuItem() throws Exception {

        loginAdmin();

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

                assert dirHome.getMenuItems().size() == 1;

                WikiMenuItem removedMenuItem = dirHome.getMenuItems().get(0);
                dirHome.removeMenuItem(removedMenuItem.getDirectoryId());

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getMenuItems().size() == 0;
            }
        }.run();
    }

    @Test
    public void addRemoveMenuItem() throws Exception {

        loginAdmin();

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

                assert dirHome.getMenuItems().size() == 1;

                WikiDirectory newMenuItem = dirHome.getAvailableMenuItems().iterator().next();
                dirHome.setSelectedChildDirectory(newMenuItem);
                dirHome.addMenuItem();

                WikiMenuItem removedMenuItem = dirHome.getMenuItems().get(0);
                dirHome.removeMenuItem(removedMenuItem.getDirectoryId());

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getMenuItems().size() == 1;
                WikiMenuItem[] menuItems = dirHome.getMenuItems().toArray(new WikiMenuItem[dirHome.getMenuItems().size()]);
                assert menuItems[0].getDirectoryId().equals(5l);
                assert menuItems[0].getDisplayPosition() == 0;
            }
        }.run();
    }

    @Test
    public void moveMenuItem() throws Exception {

        loginAdmin();

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

                assert dirHome.getMenuItems().size() == 1;

                WikiDirectory newMenuItem = dirHome.getAvailableMenuItems().iterator().next();
                dirHome.setSelectedChildDirectory(newMenuItem);
                dirHome.addMenuItem();

                dirHome.moveMenuItem(1, 0);

                assert invokeMethod("#{directoryHome.update}").equals("updated");
            }

            protected void renderResponse() throws Exception {
                DirectoryHome dirHome = (DirectoryHome)getInstance(DirectoryHome.class);
                assert dirHome.getMenuItems().size() == 2;
                WikiMenuItem[] menuItems = dirHome.getMenuItems().toArray(new WikiMenuItem[dirHome.getMenuItems().size()]);
                assert menuItems[0].getDirectoryId().equals(5l);
                assert menuItems[0].getDisplayPosition() == 0;
                assert menuItems[1].getDirectoryId().equals(4l);
                assert menuItems[1].getDisplayPosition() == 1;
            }
        }.run();
    }

    private void loginAdmin() throws Exception {
        new FacesRequest() {
           protected void invokeApplication() throws Exception {
              setValue("#{identity.username}", "admin");
              setValue("#{identity.password}", "admin");
              invokeAction("#{identity.login}");
              assert getValue("#{identity.loggedIn}").equals(true);
           }
        }.run();
    }

}
