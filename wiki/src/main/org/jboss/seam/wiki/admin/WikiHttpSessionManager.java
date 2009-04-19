/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.admin;

import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

import javax.servlet.http.HttpSession;
import javax.persistence.EntityManager;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author Christian Bauer
 */
@Name("wikiHttpSessionManager")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class WikiHttpSessionManager implements Serializable {

    protected static final String SESSION_ATTR_IDENTITY     = "org.jboss.seam.security.identity";
    protected static final String SESSION_ATTR_ACCESSLVL    = "currentAccessLevel";
    protected static final String SESSION_ATTR_USER         = "currentUser";

    @Logger
    private Log log;

    @In
    UserDAO userDAO;

    transient private Map<String, Boolean> selectedSessions = new HashMap<String,Boolean>();
    transient private Map<String, Long> sessionsSize = new HashMap<String,Long>();
    transient private List<OnlineUser> onlineMembers;

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public Map<String, Boolean> getSelectedSessions() { return selectedSessions; }
    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public Map<String, Long> getSessionsSize() { return sessionsSize; }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public List<HttpSession> getSessions() {
        return new ArrayList(WikiServletListener.getSessions().values());
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public HttpSession getSession(String id) {
        return WikiServletListener.getSessions().get(id);
    }

    /**
     * Calculate the size of an HttpSession using serialization.
     * <p>
     * This is extremely crude and a guesstimate, especially because this ignores any
     * serialization errors.
     * </p>
     *
     * @param id the identifier of th HttpSession
     * @return size in bytes
     */
    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public long getSessionSize(String id) {
        HttpSession session = WikiServletListener.getSessions().get(id);
        long sessionSize = 0;
        if (session != null) {
            Enumeration elem = session.getAttributeNames();
            while (elem.hasMoreElements()) {
                String attName = (String)elem.nextElement();
                log.debug("serializing session attribute: " + attName);
                ByteArrayOutputStream bos = null;
                try {
                    bos = new ByteArrayOutputStream();
                    ObjectOutputStream out = new ObjectOutputStream(bos);
                    out.writeObject(
                        session.getAttribute(attName)
                    );
                    out.close();
                } catch (Exception ex) {
                    // Just swallow that
                    log.warn("error during serialization, ignoring: " + ex);
                }
                if (bos != null) {
                    byte[] buf = bos.toByteArray();
                    sessionSize = sessionSize + buf.length;
                }
            }
        }
        return sessionSize;
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public String getUsername(String id) {
        log.debug("trying to get username of Http session: " + id);
        HttpSession session = WikiServletListener.getSessions().get(id);
        String username = User.GUEST_USERNAME;
        if (session != null) {
            Identity identity = (Identity)session.getAttribute(SESSION_ATTR_IDENTITY);
            if (identity != null && identity.getPrincipal() != null)
                username = identity.getPrincipal().getName();
        }
        return username;
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void calculateSelectedSessionsSize() {
        sessionsSize.clear();
        for (Map.Entry<String, Boolean> entry : selectedSessions.entrySet()) {
            if (entry.getValue()) {
                log.debug("calculating size of Http session: " + entry.getKey());
                sessionsSize.put(
                    entry.getKey(),
                    getSessionSize( entry.getKey() )
                );
            }
        }
        selectedSessions.clear();
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void refresh() {
        selectedSessions.clear();
    }

    /* TODO: The way Seam handles sessions conflicts with "destroying" it from the "outside"
    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void invalidateSelectedSessions() {
        for (Map.Entry<String, Boolean> entry : selectedSessions.entrySet()) {
            if (entry.getValue()) {
                HttpSession s = getSession(entry.getKey());
                if (s != null) {
                    log.debug("########### invalidating Http session: " + entry.getKey());
                    Session seamSession = (Session)s.getAttribute("org.jboss.seam.web.session");
                    seamSession.invalidate();
                }
            }
        }
        selectedSessions.clear();
    }
    */

    public long getTotalMembers() {
        return userDAO.findTotalNoOfUsers();
    }

    public List<OnlineUser> getOnlineMembers() {
        if (onlineMembers == null) loadOnlineMembers();
        return onlineMembers;
    }

    public long getNumberOfOnlineMembers() {
        if (onlineMembers == null) loadOnlineMembers();
        return onlineMembers.size();
    }

    public long getNumberOfOnlineGuests() {
        return WikiServletListener.getSessions().values().size() - getNumberOfOnlineMembers();
    }

    private void loadOnlineMembers() {
        onlineMembers = new ArrayList<OnlineUser>();

        // First get the usernames of members out of all sessions
        Map<String,HttpSession> onlineUsernames = new HashMap<String, HttpSession>();
        Collection<HttpSession> sessions = WikiServletListener.getSessions().values();
        for (HttpSession session : sessions) {
            Integer userLevel = (Integer)session.getAttribute(SESSION_ATTR_ACCESSLVL);
            if (userLevel != null && userLevel > Role.GUESTROLE_ACCESSLEVEL) {
                String username = ((User)session.getAttribute(SESSION_ATTR_USER)).getUsername();

                // Try to get the session with the smallest idle time
                if (onlineUsernames.containsKey(username)) {
                    try {
                        if (session.getLastAccessedTime() > onlineUsernames.get(username).getLastAccessedTime()) {
                            onlineUsernames.put(username, session);
                        }
                    } catch (IllegalStateException ex) {
                        // Just ignore that:
                        /*
                        Caused by: java.lang.IllegalStateException: getLastAccessedTime: Session already invalidated
                        at org.apache.catalina.session.StandardSession.getLastAccessedTime(StandardSession.java:439)
                        at org.apache.catalina.session.StandardSessionFacade.getLastAccessedTime(StandardSessionFacade.java:84)
                         */
                    }
                } else {
                    onlineUsernames.put(username, session);
                }
            }
        }

        // Then load these guys into a current persistence context
        List<User> userInstances = userDAO.findUsersWithUsername(onlineUsernames.keySet());

        for (User userInstance : userInstances) {
            // Now fill the OnlineUser DTO which is needed by the UI
            try {
                onlineMembers.add(
                    new OnlineUser(
                        userInstance,
                        onlineUsernames.get(userInstance.getUsername()).getLastAccessedTime()
                    )
                );
            } catch (IllegalStateException ex) {
                // Just ignore that:
                /*
                Caused by: java.lang.IllegalStateException: getLastAccessedTime: Session already invalidated
                at org.apache.catalina.session.StandardSession.getLastAccessedTime(StandardSession.java:439)
                at org.apache.catalina.session.StandardSessionFacade.getLastAccessedTime(StandardSessionFacade.java:84)
                 */
            }
        }

        Collections.sort(onlineMembers);
    }

    public static class OnlineUser implements Comparable {
        private User user;
        private long lastAccessedTime;

        public OnlineUser(User user, long lastAccessedTime) {
            this.user = user;
            this.lastAccessedTime = lastAccessedTime;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public long getLastAccessedTime() {
            return lastAccessedTime;
        }

        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }

        public String getIdleTime() {
            return WikiUtil.getTimeDifferenceToCurrent(WikiUtil.toDate(lastAccessedTime));
        }

        public int compareTo(Object o) {
            OnlineUser other = (OnlineUser) o;
            if (getLastAccessedTime() > other.getLastAccessedTime()) return -1;
            return (getLastAccessedTime() == other.getLastAccessedTime() ? 0 : 1);
        }
    }

}
