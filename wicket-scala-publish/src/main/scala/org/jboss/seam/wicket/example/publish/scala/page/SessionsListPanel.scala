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
package org.jboss.seam.wicket.example.publish.scala.page

import org.jboss.seam.wicket.example.publish.scala.bean._
import org.apache.wicket.markup.html.panel.Panel
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import org.jboss.seam.wicket.example.publish.qualifier.Sessions
import scala.reflect.BeanProperty
import org.apache.wicket.markup.html.WebComponent
import javax.inject.Inject
import java.util.{ List, ArrayList }
import org.apache.wicket.MarkupContainer
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.{ PropertyListView, ListView, ListItem }
import org.apache.wicket.model.{ Model, IModel }
import org.apache.wicket.util.lang.Generics
import org.jboss.logging.Logger
import org.jboss.seam.wicket.example.publish.scala.factory._
import org.jboss.seam.wicket.example.publish.scala.model.Article

/**
 * @author oranheim
 */
class SessionsListPanel extends Panel("sessionsPanel") {

    @Inject
    @Sessions
    var sessions: List[Session] = _

    val sessionListView = new PropertyListView[Session]("sessionsList", Generics.model(new Model[ArrayList[Session]]())) {
        override def populateItem(item: ListItem[Session]) = {
            item.add(new Label("id"))
            item.add(new Label("createdOn"))
        }
        setVersioned(true);
        setModelObject(sessions)
    }
    add(sessionListView);

}
