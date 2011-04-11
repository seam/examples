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
package org.jboss.seam.wicket.example.publish.scala.bootstrap

import org.jboss.seam.wicket.example.publish.scala.page.HomePage
import javax.inject.Inject
import org.apache.wicket.{ Page, Application }
import org.apache.wicket.settings.IMarkupSettings
import org.jboss.logging.Logger
import org.jboss.seam.wicket.SeamApplication

/**
 * @author oranheim
 */
class HomePageApplication extends SeamApplication {

    val DEVELOPMENT = true

    @Inject
    var log: Logger = _

    override def getHomePage(): Class[_ <: Page] = {
        return classOf[HomePage]
    }

    override def init() = {
        if (!DEVELOPMENT) {
            var settings = Application.get.getMarkupSettings
            settings.setCompressWhitespace(true)
            settings.setStripComments(true)
            settings.setStripWicketTags(true)
            settings.setStripXmlDeclarationFromOutput(true)
        }
    }

}
