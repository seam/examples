/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.test.plugin;

import org.dbunit.operation.DatabaseOperation;
import org.jboss.seam.wiki.test.util.DBUnitSeamTest;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.plugin.forum.ForumDAO;
import org.jboss.seam.wiki.plugin.forum.ForumInfo;
import org.jboss.seam.wiki.plugin.forum.TopicInfo;
import org.testng.annotations.Test;

import java.util.*;

public class ForumDAOTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/ForumData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void findForumsGuest() throws Exception {
        if (!database.equals(Database.mysql)) return;

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);

                Map<Long, ForumInfo> forums = dao.findForums(forumDir);

                assert forums.size() == 1;
                assert forums.get(102l).getForum().getId().equals(102l);
            }
        }.run();
    }

    @Test
    public void findForumsMember() throws Exception {
        if (!database.equals(Database.mysql)) return;

        loginMember();

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);

                Map<Long, ForumInfo> forums = dao.findForums(forumDir);

                assert forums.size() == 2;
                assert forums.get(102l).getForum().getId().equals(102l);
                assert forums.get(109l).getForum().getId().equals(109l);
            }
        }.run();

    }

    @Test
    public void findForumInfoMember() throws Exception {
        if (!database.equals(Database.mysql)) return;

        loginMember();

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);

                Map<Long, ForumInfo> infos = dao.findForums(forumDir);
                assert infos.size() == 2;

                assert infos.get(102l).getTotalNumOfTopics() == 2;
                assert infos.get(102l).getTotalNumOfPosts() == 5;
                assert infos.get(102l).getLastTopic().getId().equals(107l);
                assert infos.get(102l).getLastComment().getId().equals(106l);

                assert infos.get(109l).getTotalNumOfTopics() == 1;
                assert infos.get(109l).getTotalNumOfPosts() == 1;
                assert infos.get(109l).getLastTopic().getId().equals(111l);
                assert infos.get(109l).getLastComment() == null;
            }
        }.run();

    }

    @Test
    public void findUnreadTopicsAllForums() throws Exception {

        loginMember();

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forums = nodeDAO.findWikiDirectory(100l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);

                Calendar lastLogin = new GregorianCalendar(2007, 2, 1);
                Map<Long, Long> unreadTopics = dao.findUnreadTopicAndParentIds(forums, lastLogin.getTime());

                assert unreadTopics.size() == 3;
                assert unreadTopics.get(111l).equals(109l);
                assert unreadTopics.get(107l).equals(102l);
                assert unreadTopics.get(104l).equals(102l);

                lastLogin = new GregorianCalendar(2007, 3, 5);
                unreadTopics = dao.findUnreadTopicAndParentIds(forums, lastLogin.getTime());

                assert unreadTopics.size() == 2;
                assert unreadTopics.get(111l).equals(109l);
                assert unreadTopics.get(104l).equals(102l);
            }
        }.run();
    }

    @Test
    public void findUnreadTopicsInForum() throws Exception {

        loginMember();

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forum = nodeDAO.findWikiDirectory(102l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);

                Calendar lastLogin = new GregorianCalendar(2007, 2, 1);
                Map<Long, Long> unreadTopics = dao.findUnreadTopicAndParentIdsInForum(forum, lastLogin.getTime());

                assert unreadTopics.size() == 2;
                assert unreadTopics.get(107l).equals(102l);
                assert unreadTopics.get(104l).equals(102l);

                lastLogin = new GregorianCalendar(2007, 3, 5);
                unreadTopics = dao.findUnreadTopicAndParentIdsInForum(forum, lastLogin.getTime());

                assert unreadTopics.size() == 1;
                assert unreadTopics.get(104l).equals(102l);
            }
        }.run();
    }

    @Test
    public void findTopicCount() throws Exception {

        loginMember();

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forum = nodeDAO.findWikiDirectory(102l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);
                assert dao.findTopicCount(forum).equals(2l);

                forum = nodeDAO.findWikiDirectory(109l);
                assert dao.findTopicCount(forum).equals(1l);

            }
        }.run();

    }

    @Test
    public void findTopicsOne() throws Exception {
        if (!database.equals(Database.mysql)) return;

        loginMember();

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forum = nodeDAO.findWikiDirectory(102l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);

                Map<Long, TopicInfo> topics = dao.findTopics(forum, 0, 10);

                assert topics.size() == 2;

                assert topics.get(107l).getTopic().getId().equals(107l);
                assert topics.get(107l).getNumOfReplies() == 1l;
                assert topics.get(107l).isSticky();
                assert topics.get(107l).getLastComment().getId().equals(108l);

                assert topics.get(104l).getTopic().getId().equals(104l);
                assert topics.get(104l).getNumOfReplies() == 2l;
                assert !topics.get(104l).isSticky();
                assert topics.get(104l).getLastComment().getId().equals(106l);
            }
        }.run();
    }

    @Test
    public void findTopicsTwo() throws Exception {
        if (!database.equals(Database.mysql)) return;

        loginMember();

        new FacesRequest() {
            protected void invokeApplication() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forum = nodeDAO.findWikiDirectory(109l);

                ForumDAO dao = (ForumDAO)getInstance(ForumDAO.class);

                Map<Long, TopicInfo> topics = dao.findTopics(forum, 0, 10);

                assert topics.size() == 1;

                assert topics.get(111l).getTopic().getId().equals(111l);
                assert topics.get(111l).getNumOfReplies() == 0l;
                assert !topics.get(111l).isSticky();
                assert topics.get(111l).getLastComment() == null;
            }
        }.run();

    }

    private void loginMember() throws Exception {
        new FacesRequest() {
           protected void invokeApplication() throws Exception {
              setValue("#{identity.username}", "member");
              setValue("#{identity.password}", "member");
              invokeAction("#{identity.login}");
              assert getValue("#{identity.loggedIn}").equals(true);
           }
        }.run();
    }

}