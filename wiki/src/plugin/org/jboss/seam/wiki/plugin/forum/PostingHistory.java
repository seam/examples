package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.action.Pager;
import org.jboss.seam.wiki.core.action.UserHome;
import org.jboss.seam.wiki.core.model.WikiDirectory;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("postingHistory")
@Scope(ScopeType.PAGE)
public class PostingHistory implements Serializable {

    @In
    UserHome userHome;

    @In(create = true)
    ForumDAO forumDAO;

    private List<WikiDirectory> forumDirectories;
    private List<TopicInfo> topics;
    private Pager topicPager = new Pager("TopicPager", 15l);

    @Create
    public void onCreate() {
        forumDirectories = forumDAO.findForumDirectories();
        refreshTopics();
    }

    @Observer(value = {"TopicPager.pageChanged"}, create = false)
    public void refreshTopics() {
        countTopics();
        loadTopics();
    }

    public List<TopicInfo> getTopics() {
        return topics;
    }

    public Pager getTopicPager() {
        return topicPager;
    }

    protected void countTopics() {
        Long numOfRecords = forumDAO.findForumPostingsCount(forumDirectories, userHome.getInstance());
        topicPager.setNumOfRecords(numOfRecords);
    }

    protected void loadTopics() {

        topics =
                forumDAO.findForumPostings(
                        forumDirectories,
                        userHome.getInstance(),
                        topicPager.getQueryFirstResult(),
                        topicPager.getQueryMaxResults()
                );

    }
}
