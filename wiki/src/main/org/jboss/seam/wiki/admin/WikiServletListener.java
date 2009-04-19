/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.admin;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Christian Bauer
 */
public class WikiServletListener implements HttpSessionListener {

    private static final LogProvider log = Logging.getLogProvider(WikiServletListener.class);

    // Thread-safe read/write and non-blocking reads (snapshot reads)
    private static ConcurrentHashMap<String, HttpSession> sessions =
            new ConcurrentHashMap<String, HttpSession>();

    public void sessionCreated(HttpSessionEvent event) {
        log.debug("starting monitoring of Http session: " + event.getSession().getId());
        sessions.put(event.getSession().getId(), event.getSession());
    }

    
    public void sessionDestroyed(HttpSessionEvent event) {
        log.debug("stopping monitoring of Http session: " + event.getSession().getId());
        sessions.remove(event.getSession().getId());
    }

    public static ConcurrentHashMap<String, HttpSession> getSessions() {
        return sessions;
    }
}
