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

import org.jboss.seam.wicket.example.publish.scala.model.Article
import org.jboss.seam.wicket.example.publish.persistence.EntityDao
import javax.persistence.EntityManager
import org.jboss.seam.wicket.example.publish.qualifier.DataRepository
import org.jboss.seam.servlet.WebApplication
import javax.enterprise.event.Observes
import org.jboss.seam.servlet.event.Initialized
import javax.enterprise.inject._
import javax.inject._
import org.jboss.logging.Logger
import org.jboss.seam.wicket.example.publish.scala.bean.ArticleHome
import org.jboss.seam.wicket.example.publish.scala.model.Article
import scala.reflect.BeanProperty

/**
 * @author oranheim
 */
@Singleton
@Alternative
class ApplicationInitializer {

    @Inject
    var log: Logger = _

    @Inject
    @DataRepository
    var em: EntityManager = _

    def importData(@Observes @Initialized webapp: WebApplication) = {
        var article1 = new Article
        article1.setSubject("Welcome to Scala and CDI/Seam/Wicket")
        val body = """
            <div>
                This example demonstrates the use of Scala mixed with Java togehter with CDI/Weld, Seam Solder and Wicket.
                Scala is a general purpose programming language designed to express common programming patterns in 
                a concise, elegant, and type-safe way. It combines functional programming in combination
                with object orientation techniques, and may reduce code footprint due to the functional aspect of it.
            </div>
            <div style="padding-top: 1em;">
                This example demonstrates simple web site publishing, where you can create new postings, edit them, or
                delete them. The HttpSession observer triggers new sessions and adds to a list presented under the
                administration area.
            </div>
            <div>
                <ul>
                    <li>Scala and Java mixin development model</li>
                    <li>Wicket component model written in Scala</li>
                    <li>Arquillian test cases written in Scala</li>
                    <li>Data Access Object pattern using a non-JTA datasource to provide simplified CRUD operations</li>
                    <li>No-interface conversation beans (EJBs) (ArticleHome.scala, etc.)</li>
                    <li>Conversations (Admin ArticleListView => ArticleEditView->save)</li>
                    <li>List of entity producers (PublishFactory.scala, SessionListener.scala)</li>
                    <li>Servlet observers for HttpSession</li>
                    <li>Application initialization of database (ApplicationInitializer.scala)</li>
                    <li>Java annotations in combination with Scala Class and methods/functions</li> 
                </ul>
            </div>
        """
        article1.setBody(body.toString)

        em.getTransaction.begin
        em.persist(article1)
        em.getTransaction.commit

        log.info("Successful import of data!");
    }

}
