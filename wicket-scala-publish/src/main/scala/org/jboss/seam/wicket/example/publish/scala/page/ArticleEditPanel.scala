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

import javax.inject.Inject
import org.apache.wicket.MarkupContainer
import org.apache.wicket.markup.html._
import org.apache.wicket.markup.html.form._
import org.apache.wicket.markup.html.panel._
import org.apache.wicket.model._
import org.jboss.logging.Logger
import org.jboss.seam.wicket.example.publish.scala.bean._
import org.jboss.seam.wicket.example.publish.scala.factory._
import scala.reflect.BeanProperty

/**
 * @author oranheim
 */
class ArticleEditPanel() extends Panel("articlePanel") {

    @Inject
    var log: Logger = _

    @Inject
    @BeanProperty
    var articleHome: ArticleHome = _

    @Inject
    var factory: PublishFactory = _

    add(new Form("ArticleForm") {
        add(new FeedbackPanel("messages").setOutputMarkupId(true))

        add(new TextField("subject", new Model[String]() {
            override def getObject() =  articleHome.getInstance().getSubject()            

            override def setObject(obj: String) = articleHome.getInstance().setSubject(obj)
        }))

        add(new TextArea("body", new Model[String]() {
            override def getObject() = articleHome.getInstance.getBody

            override def setObject(obj: String) = articleHome.getInstance.setBody(obj)
        }))

        add(new Button("ArticleSubmitButton") {
            override def onSubmit() = {
                if (!articleHome.validate) {
                    if (articleHome.isInvalidSubject)
                        warn("Please provide title")
                    if (articleHome.isInvalidBody)
                        warn("Please provide body text")
                } else {
                    articleHome.save;
    
                    /* Toggle panel in view to list panel */
                    var articlePanel: Panel = getParent.getParent.asInstanceOf[Panel]
                    articlePanel.replaceWith(new ArticleListPanel)
                }
            }

        })

        add(new Button("ArticleCancelButton") {
            override def onSubmit() = {
                articleHome.cancel;

                /* Toggle panel in view to list panel */
                var articlePanel: Panel = getParent.getParent.asInstanceOf[Panel]
                articlePanel.replaceWith(new ArticleListPanel)
            }

        })
    })

}
