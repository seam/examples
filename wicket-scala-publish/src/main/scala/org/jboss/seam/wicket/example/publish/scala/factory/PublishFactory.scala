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
package org.jboss.seam.wicket.example.publish.scala.factory

import javax.enterprise.inject.Produces
import javax.inject.{ Inject, Named }
import javax.persistence.EntityManager
import java.util.List
import org.jboss.seam.wicket.example.publish.qualifier.{ DataRepository, Articles }
import org.jboss.seam.wicket.example.publish.scala.model.Article

/**
 * @author oranheim
 */
@serializable
class PublishFactory {

    @Inject
    @DataRepository
    var em: EntityManager = _

    @Produces
    @Named
    @Articles
    def getArticles(): List[Article] =
        em.createQuery("SELECT a FROM Article a ORDER BY a.createdOn DESC").getResultList.asInstanceOf[List[Article]]

}
