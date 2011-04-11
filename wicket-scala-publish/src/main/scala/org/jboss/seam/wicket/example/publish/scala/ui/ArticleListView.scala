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
package org.jboss.seam.wicket.example.publish.scala.wicket.ui

import org.jboss.seam.wicket.example.publish.scala.page.ArticleListPanel
import org.jboss.seam.wicket.example.publish.scala.page.ArticleEditPanel
import org.jboss.seam.wicket.example.publish.scala.factory.PublishFactory
import javax.inject.Inject
import java.util.List
import org.apache.wicket.MarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.link.Link
import org.apache.wicket.markup.html.list.{ PropertyListView, ListView, ListItem }
import org.apache.wicket.model.IModel
import org.jboss.logging.Logger
import org.jboss.seam.wicket.example.publish.scala.model.Article

/**
 * @author oranheim
 */
class ArticleListView(val parentContainer: MarkupContainer, val listModel: IModel[List[Article]]) extends PropertyListView[Article]("articleList", listModel) {

    @Inject
    var log: Logger = _

    @Inject
    var publishFactory: PublishFactory = _

    override def populateItem(item: ListItem[Article]) = {
        item.add(new Label("subject"))

        /* The parentContainer is HomePage and we want to display the body element */
        if (parentContainer == null) {
            item.add(new Label("body").setEscapeModelStrings(false))
        }

        /* The parentContainer is AdminPage and we have special wicket:ids in view */
        if (parentContainer != null) {
            item.add(new Label("id"))

            item.add(new Link("edit-link", item.getModel()) {
                /*
                 * Translate wicket model into the underlying object so
                 * that listener code does not need to deal with the
                 * model
                 */
                override def onClick = onEditArticle(getModelObject().asInstanceOf[Article])

            });

            item.add(new Link("delete-link", item.getModel()) {
                override def onClick = onDeleteArticle(getModelObject().asInstanceOf[Article])
            });

            def onEditArticle(entry: Article) = {
                /* swtich panel in view */
                val editPanel = new ArticleEditPanel
                editPanel.getArticleHome.setId(entry.getId())
                editPanel.getArticleHome.initConversation
                parentContainer.replaceWith(editPanel)
            }

            def onDeleteArticle(entry: Article) = {
                /* swtich panel in view */
                val editPanel = new ArticleEditPanel
                editPanel.getArticleHome.setId(entry.getId)
                editPanel.getArticleHome.delete
                parentContainer.replaceWith(new ArticleListPanel)
            }
        }

    }

    setReuseItems(true)
    setVersioned(true)
    setModelObject(publishFactory.getArticles)
}
