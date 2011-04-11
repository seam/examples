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

import org.apache.wicket.model.Model
import org.apache.wicket.model.IModel
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab
import org.apache.wicket.extensions.markup.html.tabs.ITab
import org.apache.wicket.markup.html.panel._

/**
 * @author oranheim
 */
class ISessionsTab(id: String) extends SessionsTab(id: String) with ITab {

    override def getTitle(): IModel[String] = new Model[String](id)

    //override def isVisible():java.lang.Boolean = true

    override def getPanel(panelId: String) = new SessionsTab(panelId)

}

class SessionsTab(id: String) extends Panel(id: String) {

    val listPanel = new SessionsListPanel
    add(listPanel)

}
