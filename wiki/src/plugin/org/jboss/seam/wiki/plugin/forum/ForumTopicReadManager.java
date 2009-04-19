/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * For each forum (keyed by identifier), holds a set of topic threads the user read in
 * the current session. Used to display unread topics (topics that are newer than last
 * login and that are not managed here).
 *
 * @author Christian Bauer
 */
@Name("forumTopicReadManager")
@Scope(ScopeType.SESSION)
public class ForumTopicReadManager implements Serializable {

    Map<Long, Set<Long>> readTopics = new HashMap<Long, Set<Long>>();

    public Map<Long, Set<Long>> getReadTopics() {
        return readTopics;
    }

    public void addTopicId(Long forumId, Long topicId) {
        if (readTopics.get(forumId) == null) {
            readTopics.put(forumId, new HashSet<Long>());
        }
        readTopics.get(forumId).add(topicId);
    }

    public void removeTopicId(Long forumId, Long topicId) {
        if (readTopics.get(forumId) != null) {
            readTopics.get(forumId).remove(topicId);
        }
    }

    public boolean isTopicIdRead(Long forumId, Long topicId) {
        return readTopics.get(forumId) != null && readTopics.get(forumId).contains(topicId);
    }

}
