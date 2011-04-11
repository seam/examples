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
package org.jboss.seam.wicket.example.publish.scala.persistence

import javax.enterprise.context.ConversationScoped
import javax.enterprise.inject.Produces
import javax.inject.Singleton
import javax.persistence.{ Persistence, EntityManagerFactory, EntityManager }
import org.jboss.seam.wicket.example.publish.qualifier.{ ConversationalDataRepository, DataRepository }

/**
 * @author oranheim
 */
@Singleton
class DataRepositoryProducer {

    var factory: EntityManagerFactory = _

    @Produces
    def getEntityManagerFactory: EntityManagerFactory = {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("pu")
        }
        return factory;
    }

    @Produces
    @ConversationalDataRepository
    @ConversationScoped
    def produceConversationalEntityManager() = getEntityManagerFactory.createEntityManager
    

    @Produces
    @DataRepository
    def produceEntityManager() = getEntityManagerFactory.createEntityManager

}
