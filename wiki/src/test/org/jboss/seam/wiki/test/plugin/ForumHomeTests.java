/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.plugin;

import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.plugin.forum.ForumHome;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.testng.annotations.Test;

public class ForumHomeTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/ForumData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test(groups="jdk6-expected-failures")
    public void addForum() throws Exception {

        loginAdmin();

        final String conversationId = new FacesRequest() {

            protected void updateModelValues() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);
                Contexts.getPageContext().set("currentDirectory", forumDir);
            }

            protected void invokeApplication() throws Exception {
                ForumHome home = (ForumHome)getInstance(ForumHome.class);
                home.newForum();
            }
        }.run();

        new FacesRequest() {

            protected void beforeRequest() {
                setParameter("cid", conversationId);
            }

            protected void updateModelValues() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);
                Contexts.getPageContext().set("currentDirectory", forumDir);
            }

            protected void invokeApplication() throws Exception {
                ForumHome home = (ForumHome)getInstance(ForumHome.class);

                home.getInstance().setName("New Forum");
                home.getInstance().setDescription("This is a new forum");

                assert invokeMethod("#{forumHome.persist}") == null;
            }

            protected void renderResponse() throws Exception {
                Long newId = (Long)getValue("#{forumHome.instance.id}");

                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory newForum = nodeDAO.findWikiDirectory(newId);

                assert newForum.getAreaNumber().equals(100l);

                assert newForum.getFeed() != null;

                Session s = getHibernateSession();
                WikiMenuItem newMenuItem = (WikiMenuItem)s
                        .createQuery("select m from WikiMenuItem m where m.directory.id = :dir")
                        .setParameter("dir", newId)
                        .uniqueResult();
                assert newMenuItem.getDisplayPosition() == 2l;
                s.close();

                WikiDocument defaultDoc = (WikiDocument)newForum.getDefaultFile();
                assert defaultDoc.getName().equals("New Forum Forum");
                assert defaultDoc.getAreaNumber().equals(100l);
                assert defaultDoc.getWikiname().equals("NewForumForum");
                assert defaultDoc.isNameAsTitle();
                assert defaultDoc.getReadAccessLevel() == 0;
                assert defaultDoc.getWriteAccessLevel() == Role.ADMINROLE_ACCESSLEVEL;
                assert defaultDoc.getCreatedBy().getUsername().equals(User.ADMIN_USERNAME);
                assert !defaultDoc.isEnableCommentForm();
                assert !defaultDoc.isEnableComments();
                assert !defaultDoc.isEnableCommentsOnFeeds();
                assert defaultDoc.getHeaderMacrosString().contains("clearBackground");
                assert defaultDoc.getHeaderMacrosString().contains("hideControls");
                assert defaultDoc.getHeaderMacrosString().contains("hideComments");
                assert defaultDoc.getHeaderMacrosString().contains("hideTags");
                assert defaultDoc.getHeaderMacrosString().contains("hideComments");
                assert defaultDoc.getHeaderMacrosString().contains("clearBackground");
                assert defaultDoc.getContentMacrosString().contains("forumTopics");
                assert defaultDoc.getFooterMacrosString() == null;
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

    private Session getHibernateSession() throws Exception {
        org.jboss.ejb3.entity.InjectedEntityManagerFactory jbossEMF =
                (org.jboss.ejb3.entity.InjectedEntityManagerFactory) getInitialContext().lookup("java:/entityManagerFactories/wiki");
        return ((HibernateEntityManagerFactory) jbossEMF.getDelegate()).getSessionFactory().openSession();
    }


}