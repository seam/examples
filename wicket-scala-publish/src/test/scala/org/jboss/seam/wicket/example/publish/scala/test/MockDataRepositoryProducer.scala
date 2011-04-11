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
package org.jboss.seam.wicket.example.publish.scala.test

import org.jboss.logging.Logger
import javax.inject.Inject
import javax.enterprise.context.RequestScoped
import org.jboss.seam.wicket.example.publish.scala.persistence.DataRepositoryProducer
import javax.enterprise.inject.Alternative
import javax.enterprise.context.NormalScope
import javax.enterprise.context.Dependent
import org.jboss.seam.solder.core.Veto
import org.jboss.seam.wicket.example.publish.qualifier.DataRepository
import org.jboss.seam.wicket.example.publish.qualifier.ConversationalDataRepository
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Provides a non-conversational EntityMnager for plain JPA testing
 *
 * @author oranheim
 */
@Singleton
//@Alternative
class MockDataRepositoryProducer {
    
    @Inject
    var log: Logger = _
    
    var factory: EntityManagerFactory = _

    @Produces
    def getEntityManagerFactory(): EntityManagerFactory = {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("pu")
        }
        return factory;
    }

    @Produces
    @ConversationalDataRepository
    def produceConversationalEntityManager(): EntityManager = getEntityManagerFactory.createEntityManager
    

    @Produces
    @DataRepository
    def produceEntityManager(): EntityManager = getEntityManagerFactory.createEntityManager
    

}
