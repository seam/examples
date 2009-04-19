package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.ScopeType;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import java.util.*;
import java.io.Serializable;

@Name("forumDAO")
@Scope(ScopeType.CONVERSATION)
public class ForumDAO implements Serializable {

    @In
    EntityManager entityManager;

    @In
    EntityManager restrictedEntityManager;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    Integer currentAccessLevel;

    public List<WikiDirectory> findForumDirectories() {
        return getSession(true).getNamedQuery("forumDirectories")
                .setComment("Finding forum directories")
                .list();
    }

    public Long findForumPostingsCount(List<WikiDirectory> forumDirectories, User user) {
        if (forumDirectories == null || forumDirectories.size() == 0) {
            return 0l;
        } else {
            return (Long) getSession(true).getNamedQuery("forumTopicsForUserCount")
                    .setParameterList("parentDirectories", forumDirectories)
                    .setParameter("user", user)
                    .setComment("Finding forum topcis count for user: " + user)
                    .uniqueResult();
        }
    }

    public List<TopicInfo> findForumPostings(List<WikiDirectory> forumDirectories, User user, int firstResult, int maxResults) {

        final Map<Long, TopicInfo> topicInfoMap = new LinkedHashMap();

        if (forumDirectories == null || forumDirectories.size() == 0) {
            return Collections.EMPTY_LIST;
        }

        getSession(true).getNamedQuery("forumTopicsForUser")
                .setParameterList("parentDirectories", forumDirectories)
                .setParameter("user", user)
                .setComment("Finding forum topcis for user: " + user)
                .setResultTransformer(
                        new ResultTransformer() {
                            public Object transformTuple(Object[] result, String[] strings) {
                                WikiDocument doc = (WikiDocument) result[0];
                                topicInfoMap.put(
                                        doc.getId(),
                                        new TopicInfo(doc)
                                );
                                return null;
                            }
                            public List transformList(List list) { return list; }
                        }
                )
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .list();

        if (topicInfoMap.size() > 0) {
            getSession(true).getNamedQuery("forumTopicsReplies")
                .setParameterList("topicIds", topicInfoMap.keySet())
                .setComment("Retrieving forum topic replies")
                .setResultTransformer(
                    new ResultTransformer() {
                        public Object transformTuple(Object[] result, String[] strings) {
                            if (topicInfoMap.containsKey((Long)result[1])) {
                                TopicInfo info = topicInfoMap.get( (Long)result[1] );
                                info.setNumOfReplies((Long)result[2]);
                                info.setLastComment((WikiComment)result[0]);
                            }
                            return null;
                        }
                        public List transformList(List list) { return list; }
                    }
                )
                .list();
        }

        return new ArrayList(topicInfoMap.values());
    }


    public List<WikiMenuItem> findForumsMenuItems(WikiDirectory forumsDirectory) {
        return getSession(true).getNamedQuery("forumsMenuItems")
                .setParameter("parentDir", forumsDirectory)
                .list();
    }

    public boolean findForumsAvailability(WikiDirectory forumsDirectory) {
        Long forumsCount = (Long)
            getSession(true).getNamedQuery("forumsCount")
                .setParameter("parentDir", forumsDirectory)
                .setComment("Counting all forums")
                .uniqueResult();
        return forumsCount > 0l;
    }

    public Map<Long, ForumInfo> findForums(WikiDirectory forumsDirectory) {
        final Map<Long, ForumInfo> forumInfoMap = new LinkedHashMap<Long, ForumInfo>();

        getSession(true).getNamedQuery("forums")
            .setParameter("parentDir", forumsDirectory)
            .setComment("Finding all forums")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        forumInfoMap.put(
                            (Long) result[0],
                            new ForumInfo( (WikiDirectory)result[1])
                        );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Find topic count (topics are just wiki documents in the forum directories)
        getSession(true).getNamedQuery("forumTopicCount")
            .setParameter("parentDir", forumsDirectory)
            .setComment("Finding topic count for all forums")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0])) {
                            ForumInfo info = forumInfoMap.get( (Long)result[0] );
                            info.setTotalNumOfTopics((Long)result[1]);
                            info.setTotalNumOfPosts(info.getTotalNumOfTopics());
                        }
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Add reply count to topic count to get total num of posts
        getSession(true).getNamedQuery("forumReplyCount")
            .setParameter("parentDirId", forumsDirectory.getId())
            .setParameter("readAccessLevel", currentAccessLevel)
            .setComment("Finding reply count for all forums")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0])) {
                            ForumInfo info = forumInfoMap.get( (Long)result[0] );
                            info.setTotalNumOfPosts(
                                info.getTotalNumOfPosts() + (Long)result[1]
                            );
                        }
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Append last topic WikiDocument (faster if we do it with a MySQL specific LIMIT subselect)
        List<Object[]> forumsAndLastTopics = getSession(true).getNamedQuery("forumLastTopic")
            .setParameter("parentDirId", forumsDirectory.getId())
            .setParameter("readAccessLevel", currentAccessLevel)
            .setComment("Finding last topics for all forums")
            .list();
        for (Object[] lastTopicRow : forumsAndLastTopics) {
            if (forumInfoMap.containsKey((Long)lastTopicRow[0])) {
                WikiDocument lastTopic = wikiNodeDAO.findWikiDocument( (Long)lastTopicRow[1] );
                forumInfoMap.get( (Long)lastTopicRow[0] ).setLastTopic( lastTopic );
            }
        }

        // Append last reply WikiComment
        for (final Long forumId : forumInfoMap.keySet()) {
            getSession(true).getNamedQuery("forumLastReply")
                    .setParameter("parentDirId", forumId)
                    .setParameter("readAccessLevel", currentAccessLevel)
                    .setComment("Finding last replies for forum : " + forumId)
                    .setResultTransformer(
                        new ResultTransformer() {
                            public Object transformTuple(Object[] result, String[] strings) {
                                forumInfoMap.get(forumId).setLastComment( (WikiComment)result[0] );
                                return null;
                            }
                            public List transformList(List list) { return list; }
                        }
                    )
                    .list();
        }
        return forumInfoMap;
    }

    public Map<Long, Long> findUnreadTopicAndParentIds(WikiDirectory forumsDir, Date lastLoginDate) {
        return findUnreadTopicAndParentIds("forumUnreadTopics", "forumUnreadReplies", forumsDir, lastLoginDate);
    }

    public Map<Long, Long> findUnreadTopicAndParentIdsInForum(WikiDirectory forum, Date lastLoginDate) {
        return findUnreadTopicAndParentIds("forumUnreadTopicsInForum", "forumUnreadRepliesInForum", forum, lastLoginDate);
    }

    private Map<Long, Long> findUnreadTopicAndParentIds(String unreadTopicsQuery, String unreadRepliesQuery,
                                                        WikiDirectory directory, Date lastLoginDate) {
        final Map<Long, Long> unreadTopics = new HashMap<Long, Long>();

        getSession(true).getNamedQuery(unreadTopicsQuery)
            .setParameter("parentDir", directory)
            .setParameter("lastLoginDate", lastLoginDate)
            .setComment("Finding unread topics")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] objects, String[] strings) {
                        unreadTopics.put((Long)objects[0], (Long)objects[1]);
                        return null;
                    }
                    public List transformList(List list) { return list;}
                }
            )
            .list();

        getSession(true).getNamedQuery(unreadRepliesQuery)
            .setParameter("parentDirId", directory.getId())
            .setParameter("readAccessLevel", currentAccessLevel)
            .setParameter("lastLoginDate", lastLoginDate)
            .setComment("Finding unread replies")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] objects, String[] strings) {
                        unreadTopics.put((Long)objects[0], (Long)objects[1]);
                        return null;
                    }
                    public List transformList(List list) { return list;}
                }
            )
            .list();

        return unreadTopics;
    }

    public Long findTopicCount(WikiDirectory forum) {
        return (Long)getSession(true).getNamedQuery("forumTopicsCount")
                .setParameter("parentDir", forum)
                .setComment("Retrieving forum topics count")
                .setCacheable(true)
                .uniqueResult();
    }

    public Map<Long, TopicInfo> findTopics(WikiDirectory forum, long firstResult, long maxResults) {

        // Limited list of topics, first retrieve identifiers only (faster on ORDER BY/LIMIT) then
        // batch select the topic instances, then batch select the reply instances, we collect all
        // of this stuff in this map:
        final Map<Long, TopicInfo> topicInfoMap = new LinkedHashMap<Long, TopicInfo>();

        // Retrieve topic identifier, sticky? and hasReplies? data
        getSession(true).getNamedQuery("forumTopicsList")
            .setParameter("parentNodeId", forum.getId())
            .setParameter("readAccessLevel", currentAccessLevel)
            .setComment("Retrieving forum topics list")
            .setFirstResult(new Long(firstResult).intValue())
            .setMaxResults(new Long(maxResults).intValue())
            .setResultTransformer(
                    new ResultTransformer() {
                        public Object transformTuple(Object[] result, String[] strings) {
                            Long topicId = (Long) result[0];
                            Integer sticky = (Integer)result[1];
                            Boolean hasReplies = (Boolean)result[2];
                            topicInfoMap.put(topicId, new TopicInfo(sticky, hasReplies));
                            return null;
                        }
                        public List transformList(List list) { return list; }
                    }
            )
            .list();

        if (topicInfoMap.keySet().size() == 0) return topicInfoMap; // Early exist possible

        // Retrieve the topic entity instances and shove them into the map
        getSession(true).getNamedQuery("forumTopics")
            .setParameterList("topicIds", topicInfoMap.keySet())
            .setComment("Retrieving forum topic list instances")
            .setResultTransformer(
                    new ResultTransformer() {
                        public Object transformTuple(Object[] result, String[] strings) {
                            WikiDocument topicInstance = (WikiDocument)result[0];
                            topicInfoMap.get(topicInstance.getId()).setTopic(topicInstance);
                            return null;
                        }
                        public List transformList(List list) { return list; }
                    }
            )
            .list();


        // Figure out which and if we even should query the reply instances
        List<Long> topicIdsWithReplies = new ArrayList<Long>();
        for (Map.Entry<Long, TopicInfo> entry : topicInfoMap.entrySet()) {
            if (entry.getValue().isReplies()) topicIdsWithReplies.add(entry.getKey());
        }

        if (topicIdsWithReplies.size() == 0) return topicInfoMap; // Early exit possible
        
        getSession(true).getNamedQuery("forumTopicsReplies")
            .setParameterList("topicIds", topicIdsWithReplies)
            .setComment("Retrieving forum topic replies")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (topicInfoMap.containsKey((Long)result[1])) {
                            TopicInfo info = topicInfoMap.get( (Long)result[1] );
                            info.setNumOfReplies((Long)result[2]);
                            info.setLastComment((WikiComment)result[0]);
                        }
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        return topicInfoMap;
    }

    public List<User> findPostersAndRatingPoints(Long forumId, int maxResults, List<String> excludeRoles) {

        if (excludeRoles.size() == 0) {
            excludeRoles.add("guest"); // By default, don't show guests, query requires _some_ exclude
        }

        final List<User> postersAndRatingPoints = new ArrayList<User>();

        getSession(true).getNamedQuery("forumPostersAndRatingPoints")
            .setParameter("parentDirId", forumId)
            .setParameterList("ignoreUserInRoles", excludeRoles )
            .setMaxResults(maxResults)
            .setComment("Retrieving forum posters and rating points")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        ((User)result[0]).setRatingPoints((Long)result[1]);
                        postersAndRatingPoints.add((User)result[0]);
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();
        return postersAndRatingPoints;
    }

    private Session getSession(boolean restricted) {
        if (restricted) {
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
        } else {
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) entityManager).getDelegate());
        }
    }
}
