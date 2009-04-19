package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiComment;
import org.jboss.seam.wiki.core.model.WikiDirectory;

public class TopicInfo {

    private WikiDirectory forum;
    private WikiDocument topic;
    private boolean unread;
    private boolean sticky;
    private boolean replies;
    private long numOfReplies;
    private WikiComment lastComment;

    public TopicInfo(Integer sticky, boolean replies) {
        this.sticky = sticky != 0;
        this.replies = replies;
    }

    public TopicInfo(WikiDocument topic) {
        this.forum = (WikiDirectory) topic.getParent();
        this.topic = topic;
    }

    public void setTopic(WikiDocument topic) {
        this.topic = topic;
    }

    public WikiDocument getTopic() {
        return topic;
    }

    public WikiDirectory getForum() {
        return forum;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isReplies() {
        return replies;
    }

    public void setReplies(boolean replies) {
        this.replies = replies;
    }

    public long getNumOfReplies() {
        return numOfReplies;
    }

    public void setNumOfReplies(long numOfReplies) {
        this.numOfReplies = numOfReplies;
    }

    public WikiComment getLastComment() {
        return lastComment;
    }

    public void setLastComment(WikiComment lastComment) {
        this.lastComment = lastComment;
    }

    public String getIconName() {
        StringBuilder iconName = new StringBuilder();
        iconName.append("posting");
        if (isSticky()) {
            iconName.append("_sticky");
        } else if (!topic.isEnableCommentForm()) {
            iconName.append("_locked");
        }
        if (isUnread()) iconName.append("_unread");

        return iconName.toString();
    }


    public String toString() {
        return "TopicInfo(" + getTopic().getId() +
                ") replies: " + getNumOfReplies() +
                ", unread: " + isUnread() +
                ", sticky: " + isSticky() +
                ", last comment: " + getLastComment();
    }

}
