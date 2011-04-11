/*
* JBoss, Home of Professional Open Source
* Copyright 2010, Red Hat Middleware LLC, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.jboss.seam.wicket.example.publish.scala.bean

import scala.collection.mutable.ConcurrentMap
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConversions._
import java.util.ArrayList
import java.util.Date
import java.util.List

import javax.enterprise.context.RequestScoped
import javax.enterprise.event.Observes
import javax.enterprise.inject.Produces
import javax.inject.Inject
import javax.inject.Singleton
import javax.servlet.http.HttpSession

import org.jboss.logging.Logger
import org.jboss.seam.servlet.event.Destroyed
import org.jboss.seam.servlet.event.Initialized
import org.jboss.seam.wicket.example.publish.qualifier.Sessions

/**
 * @author oranheim
 */
@Singleton
class SessionListener {

    @Inject
    var log: Logger = _

    def observeSessionInitialized(@Observes@Initialized session: HttpSession) =
        SessionListener.sessions.put(session.getId, new Date(session.getCreationTime))
    

    def observeSessionDestroyed(@Observes@Destroyed session: HttpSession) = 
        SessionListener.sessions.remove(session.getId)

    @Produces
    @RequestScoped
    @Sessions
    def getSessions(): List[Session] = {
        var sessionList: List[Session] = new ArrayList[Session]();
        SessionListener.sessions.foreach { tuple: (String, Date) =>
            val (key, value) = tuple

            val session = new Session
            session.setId(key)
            session.setCreatedOn(value)
            sessionList.add(session)
        }
        return sessionList
    }

}

object SessionListener {
    var sessions: ConcurrentMap[String, Date] = new ConcurrentHashMap[String, Date]
}
