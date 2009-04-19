package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiDirectory;

public class ForumInfo {

    private WikiDirectory forum;
    private boolean unreadPostings = false;
    private long totalNumOfTopics;
    private long totalNumOfPosts;
    private WikiDocument lastTopic;
    private WikiComment lastComment;

    public ForumInfo(WikiDirectory forum) {
        this.forum = forum;
    }

    public WikiDirectory getForum() {
        return forum;
    }

    public boolean isUnreadPostings() {
        return unreadPostings;
    }

    public void setUnreadPostings(boolean unreadPostings) {
        this.unreadPostings = unreadPostings;
    }

    public long getTotalNumOfTopics() {
        return totalNumOfTopics;
    }

    public void setTotalNumOfTopics(long totalNumOfTopics) {
        this.totalNumOfTopics = totalNumOfTopics;
    }

    public long getTotalNumOfPosts() {
        return totalNumOfPosts;
    }

    public void setTotalNumOfPosts(long totalNumOfPosts) {
        this.totalNumOfPosts = totalNumOfPosts;
    }

    public WikiDocument getLastTopic() {
        return lastTopic;
    }

    public void setLastTopic(WikiDocument lastTopic) {
        this.lastTopic = lastTopic;
    }

    public WikiComment getLastComment() {
        return lastComment;
    }

    public void setLastComment(WikiComment lastComment) {
        this.lastComment = lastComment;
    }

    // Was the last post made a topic or a comment/reply
    public boolean isLastPostLastTopic() {
        if (lastComment == null && lastTopic != null) return true;
        if (lastTopic != null && (lastTopic.getCreatedOn().getTime()>lastComment.getCreatedOn().getTime()) ) return true;
        return false;
    }

    public String toString() {
        return "ForumInfo(" + getForum().getId() +
                ") topics: " + getTotalNumOfTopics() +
                ", posts: " + getTotalNumOfPosts() +
                ", last topic: " + getLastTopic() +
                ", last comment: " + getLastComment();
    }
}
