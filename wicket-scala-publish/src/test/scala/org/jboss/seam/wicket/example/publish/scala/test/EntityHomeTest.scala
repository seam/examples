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

import org.jboss.seam.solder.core.Veto
import org.jboss.arquillian.impl.event.FiredEventException
import java.util.ServiceLoader
import org.jboss.arquillian.spi.DeployableContainer
import javax.inject.Inject
import javax.persistence.EntityManager
import org.jboss.arquillian.api.Deployment
import org.jboss.arquillian.junit.Arquillian
import org.jboss.logging.Logger
import org.jboss.seam.wicket.example.publish.persistence._
import org.jboss.seam.wicket.example.publish.qualifier._
import org.jboss.seam.wicket.example.publish.scala.factory._
import org.jboss.seam.wicket.example.publish.scala.model._
import org.jboss.seam.wicket.example.publish.scala.bean._
import org.jboss.seam.wicket.example.publish.scala.persistence._
import org.jboss.shrinkwrap.api.{ ShrinkWrap, ArchivePaths }
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.weld.context.ContextNotActiveException
import org.junit._
import org.junit.runner.RunWith

/**
 * @author oranheim
 */
@RunWith(classOf[Arquillian])
class EntityHomeTest {

    @Inject
    var log: Logger = _

    @Inject
    var publishFactory: PublishFactory = _
    
    @Inject
    var articleHome: ArticleHome = _
    
    @Test(expected = classOf[FiredEventException])
    def testDummy() = {
        // do nothing and look into reason behind ContextNotActiveException
        // uncomment other tests and run mvn clean test -Pitest-jetty-embedded -Dtest=EntityHomeTest
    }

    //@Test
    def shouldAddArticleEntryButThrowsContextNotActiveExceptionWhichShouldNotHappen() = {
        try {
            Assert.assertNotNull(articleHome)
            
            articleHome.getInstance.setSubject("Hello")
            articleHome.getInstance.setBody("World")
            
            articleHome.save
            
            Assert.assertNotNull(articleHome.getInstance.getId)
        } catch {
            case e: ContextNotActiveException => println(e.printStackTrace)
            case e: IllegalStateException => println(e.printStackTrace)
            case e: FiredEventException => println(e.printStackTrace)
            
        }
    }


}
object EntityHomeTest {

    @Deployment
    def createTestArchive(): WebArchive = {
        var war = ShrinkWrap.create(classOf[WebArchive], "test.war")
            .addClasses(classOf[DataRepository], classOf[ConversationalDataRepository], classOf[DataRepositoryProducer])
            .addClasses(classOf[BaseEntity], classOf[Article], classOf[PublishFactory], classOf[Articles])
            .addClasses(classOf[CustomEntityHome[_]], classOf[EntityHome[_]], classOf[EntityDao], classOf[ArticleHome])
            .addLibraries(
                MavenArtifactResolver.resolve("org.hibernate:hibernate-entitymanager:3.6.0.Final"),
                MavenArtifactResolver.resolve("javax.transaction:jta:1.1"))
                .addWebResource("test-persistence.xml", "classes/META-INF/persistence.xml")
                .addWebResource("test-jetty-env.xml", "jetty-env.xml")
                .addWebResource("test-beans.xml", ArchivePaths.create("beans.xml"))
                .setWebXML("test-web.xml")
                .addManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"));
        //if (!ServiceLoader.load(classOf[DeployableContainer]).iterator().next().getClass().getName().contains("embedded")) {
        //    war.addLibrary(MavenArtifactResolver.resolve("org.jboss.seam.solder:seam-solder:3.0.0.Final"));
        //}
        return war
    }

}
