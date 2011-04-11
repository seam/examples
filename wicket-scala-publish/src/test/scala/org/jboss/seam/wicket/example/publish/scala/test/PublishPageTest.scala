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

import org.apache.wicket.util.tester.WicketTesterHelper
import org.jboss.weld.context.ContextNotActiveException
import org.junit.Assert
import org.apache.wicket.util.tester.FormTester
import org.jboss.arquillian.spi.DeployableContainer
import java.util.ServiceLoader
import org.jboss.seam.wicket.util.NonContextual
import org.jboss.seam.wicket.SeamApplication
import org.jboss.seam.wicket.example.publish.qualifier.ConversationalDataRepository
import org.jboss.seam.wicket.example.publish.qualifier.DataRepository
import org.jboss.seam.wicket.example.publish.scala.wicket.ui.ArticleListView
import org.jboss.seam.wicket.example.publish.qualifier.Articles
import org.jboss.seam.wicket.example.publish.scala.bean.ArticleHome
import org.jboss.shrinkwrap.api.ArchivePaths
import java.io.File
import org.jboss.seam.wicket.example.publish.scala.page._
import org.jboss.seam.wicket.example.publish.scala.bootstrap.HomePageApplication
import org.jboss.seam.wicket.mock.SeamWicketTester
import org.junit.Test
import javax.annotation.PostConstruct
import org.jboss.seam.wicket.example.publish.persistence._
import org.jboss.seam.wicket.example.publish.scala.persistence._
import org.jboss.shrinkwrap.api.ArchivePaths
import org.jboss.shrinkwrap.api.asset.EmptyAsset
import org.jboss.seam.wicket.example.publish.scala.model._
import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.arquillian.api.Deployment
import javax.persistence.EntityManager
import org.jboss.logging.Logger
import org.jboss.arquillian.junit.Arquillian
import org.jboss.seam.wicket.example.publish.scala.factory.PublishFactory
import javax.inject.Inject
import org.junit.runner.RunWith

/**
 * @author oranheim
 */
@RunWith(classOf[Arquillian])
class PublishPageTest {

    @Inject
    var log: Logger = _

    @Inject
    @DataRepository
    var em: EntityManager = _

    @Inject
    var factory: PublishFactory = _

    @Inject
    var tester: SeamWicketTester = _

    def getComponentValue(path: String):String = {
        val lastRenderedPage = WicketTesterHelper.getComponentData(tester.getLastRenderedPage()).toArray
        List.fromArray(lastRenderedPage).foreach(i => {
            val component = i.asInstanceOf[WicketTesterHelper.ComponentData]
            val compnentPath = component.path
            if (path.equals(compnentPath)) {
                println(component.value)
                return component.value
            }
        })
        return null
    }

    @Test
    def testClickOnAdminPage() {
        try {
            tester.startPage(classOf[HomePage]);
            tester.assertRenderedPage(classOf[HomePage])
    
            tester.clickLink("Admin")
            tester.assertRenderedPage(classOf[AdminPage])
        } catch {
            case e: ContextNotActiveException => println(e.printStackTrace)
        }
    }

    @Test
    def testPublishArticleFromAdminPage() {
        try {
            tester.startPage(classOf[AdminPage])
            tester.assertRenderedPage(classOf[AdminPage])
    
            tester.assertVisible("AdminForm:tabs:panel:articlePanel:create-link")
    
            var adminForm: FormTester = tester.newFormTester("AdminForm")
            Assert.assertNotNull(adminForm)
            
            adminForm.submitLink("tabs:panel:articlePanel:create-link", false)
            tester.assertRenderedPage(classOf[AdminPage])
            
            adminForm.setValue("tabs:panel:articlePanel:ArticleForm:subject", "Hello")
            adminForm.setValue("tabs:panel:articlePanel:ArticleForm:body", "World")
    
            adminForm.submit("tabs:panel:articlePanel:ArticleForm:ArticleSubmitButton")

            Assert.assertEquals("WELD-001303 No active contexts for scope type javax.enterprise.context.ConversationScoped", 
                    getComponentValue("AdminForm:tabs:panel:articlePanel:ArticleForm:subject"))
            
            Assert.assertEquals("WELD-001303 No active contexts for scope type javax.enterprise.context.ConversationScoped", 
                    getComponentValue("AdminForm:tabs:panel:articlePanel:ArticleForm:body"))
                    
            //tester.dumpPage
            //tester.debugComponentTrees
        } catch {
            case e: ContextNotActiveException => println(e.printStackTrace)
        }
    }

}

object PublishPageTest {

    @Deployment
    def createTestArchive(): WebArchive = {
        var war = ShrinkWrap.create(classOf[WebArchive], "test.war")
            .addPackage(classOf[SeamApplication].getPackage())
            .addPackage(classOf[NonContextual[_]].getPackage())
            .addPackage(classOf[SeamWicketTester].getPackage())

            .addPackage(classOf[EntityDao].getPackage())
            .addPackage(classOf[Articles].getPackage())
            .addPackage(classOf[ArticleHome].getPackage())
            .addPackage(classOf[HomePageApplication].getPackage())
            .addPackage(classOf[PublishFactory].getPackage())
            .addPackage(classOf[Article].getPackage())
            .addPackage(classOf[HomePage].getPackage())
            .addPackage(classOf[EntityHome[_]].getPackage())
            .addPackage(classOf[ArticleListView].getPackage())

            .addLibraries(
                MavenArtifactResolver.resolve("org.apache.wicket:wicket:1.4.15"),
                MavenArtifactResolver.resolve("org.hibernate:hibernate-entitymanager:3.6.0.Final"),
                MavenArtifactResolver.resolve("javax.transaction:jta:1.1"))
                .addWebResource("test-persistence.xml", "classes/META-INF/persistence.xml")
                .addWebResource("test-jetty-env.xml", "jetty-env.xml")
                .addWebResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                .setWebXML("test-web.xml")
                .addManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/BasePage.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/BasePage.html")
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/HomePage.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/HomePage.html")
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/AdminPage.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/AdminPage.html")
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/ArticleListPanel.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/ArticleListPanel.html")
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/ArticleEditPanel.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/ArticleEditPanel.html")
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/ArticleTab.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/ArticleTab.html")
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/SessionsListPanel.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/SessionsListPanel.html")
                .addResource(new File("src/main/scala/org/jboss/seam/wicket/example/publish/scala/page/SessionsTab.html"), "WEB-INF/classes/org/jboss/seam/wicket/example/publish/scala/page/SessionsTab.html")
        if (!ServiceLoader.load(classOf[DeployableContainer]).iterator().next().getClass().getName().contains("embedded")) {
            war.addLibrary(MavenArtifactResolver.resolve("org.jboss.seam.solder:seam-solder:3.0.0.Final"));
        }
        return war
    }

}
