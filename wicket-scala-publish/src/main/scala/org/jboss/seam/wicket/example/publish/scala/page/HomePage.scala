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

import org.jboss.seam.wicket.example.publish.scala.wicket.ui._
import org.jboss.seam.wicket.example.publish.scala.model.Article
import java.util.ArrayList
import org.apache.wicket.model.Model
import org.apache.wicket.util.lang.Generics
import javax.inject.Inject
import org.apache.wicket.Component
import org.apache.wicket.markup.html.WebMarkupContainer
import org.jboss.seam.wicket.example.publish.scala.bootstrap.ApplicationInitializer

/**
 * @author oranheim
 */
class HomePage extends BasePage {

    add(new ArticleListView(null, Generics.model(new Model[ArrayList[Article]]())))
    setOutputMarkupId(true)

}
