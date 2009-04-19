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
import org.jboss.seam.wiki.plugin.forum.*;
import org.jboss.seam.contexts.Contexts;
import org.testng.annotations.Test;

import java.util.*;

public class ForumQueryTests extends DBUnitSeamTest {

    protected void prepareDBUnitOperations() {
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/WikiBaseData.dbunit.xml", DatabaseOperation.CLEAN_INSERT)
        );
        beforeTestOperations.add(
            new DataSetOperation("org/jboss/seam/wiki/test/ForumData.dbunit.xml", DatabaseOperation.INSERT)
        );
    }

    @Test
    public void findForums() throws Exception {
        if (!database.equals(Database.mysql)) return;

        loginMember();

        new FacesRequest() {

            protected void updateModelValues() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(100l);
                Contexts.getPageContext().set("currentDirectory", forumDir);
            }

            protected void invokeApplication() throws Exception {

                ForumQuery query = (ForumQuery)getInstance(ForumQuery.class);
                List<ForumInfo> forums = query.getForums();

                assert forums.size() == 2;

                assert forums.get(0).getForum().getId().equals(102l);
                assert forums.get(0).getTotalNumOfTopics() == 2;
                assert forums.get(0).getTotalNumOfPosts() == 5;
                assert forums.get(0).getLastTopic().getId().equals(107l);
                assert forums.get(0).getLastComment().getId().equals(106l);
                assert forums.get(0).isUnreadPostings();

                assert forums.get(1).getForum().getId().equals(109l);
                assert forums.get(1).getTotalNumOfTopics() == 1;
                assert forums.get(1).getTotalNumOfPosts() == 1;
                assert forums.get(1).getLastTopic().getId().equals(111l);
                assert forums.get(1).getLastComment() == null;
                assert forums.get(1).isUnreadPostings();
            }
        }.run();
    }

    @Test
    public void findTopicsOne() throws Exception {
        if (!database.equals(Database.mysql)) return;

        loginMember();

        new FacesRequest() {

            protected void updateModelValues() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(102l);
                Contexts.getPageContext().set("currentDirectory", forumDir);
            }

            protected void invokeApplication() throws Exception {

                ForumQuery query = (ForumQuery)getInstance(ForumQuery.class);
                List<TopicInfo> topics = query.getTopics();

                assert topics.size() == 2;

                assert topics.get(0).getTopic().getId().equals(107l);
                assert topics.get(0).getNumOfReplies() == 1l;
                assert topics.get(0).isSticky();
                assert topics.get(0).getLastComment().getId().equals(108l);
                assert topics.get(0).isUnread();

                assert topics.get(1).getTopic().getId().equals(104l);
                assert topics.get(1).getNumOfReplies() == 2l;
                assert !topics.get(1).isSticky();
                assert topics.get(1).getLastComment().getId().equals(106l);
                assert topics.get(0).isUnread();
            }
        }.run();
    }

    @Test
    public void findTopicsTwo() throws Exception {
        if (!database.equals(Database.mysql)) return;

        loginMember();

        new FacesRequest() {

            protected void updateModelValues() throws Exception {
                WikiNodeDAO nodeDAO = (WikiNodeDAO)getInstance(WikiNodeDAO.class);
                WikiDirectory forumDir = nodeDAO.findWikiDirectory(109l);
                Contexts.getPageContext().set("currentDirectory", forumDir);
            }

            protected void invokeApplication() throws Exception {

                ForumQuery query = (ForumQuery)getInstance(ForumQuery.class);
                List<TopicInfo> topics = query.getTopics();

                assert topics.size() == 1;

                assert topics.get(0).getTopic().getId().equals(111l);
                assert topics.get(0).getNumOfReplies() == 0l;
                assert !topics.get(0).isSticky();
                assert topics.get(0).getLastComment() == null;
                assert topics.get(0).isUnread();
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