package org.jboss.seam.security.examples.openid;


import javax.inject.Inject;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.security.examples.openidClient.OpenIdTestClient;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test class for HomePage and SeamWicketTester.
 *
 * @author oranheim
 */
@RunWith(Arquillian.class)
public class DeploymentTest {

    @Deployment
    public static WebArchive createTestArchive() {

        WebArchive archive = ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackage(OpenIdProviderSpiImpl.class.getPackage())
                .addClass(OpenIdTestClient.class)
                .addWebResource("WEB-INF/beans.xml", "beans.xml")
                .addWebResource("WEB-INF/faces-config.xml", "faces-config.xml")
                        //Jsf pages particpating in login process
                .addResource("Login.xhtml", "Login.xhtml")
                .addResource("PageTemplate.xhtml", "PageTemplate.xhtml")
                .addResource("Attributes.xhtml", "Attributes.xhtml")
                .addResource("Menu.xhtml", "Menu.xhtml")
                .addResource("openIdProviderCustomizer.properties", "WEB-INF/classes/openIdProviderCustomizer.properties")
                .addLibraries(MavenArtifactResolver.resolve("org.jboss.seam.security:seam-security-external"))

//      .addLibraries(MavenArtifactResolver.resolve("org.picketlink.idm:picketlink-idm-api"))
//      .addLibraries(MavenArtifactResolver.resolve("org.picketlink.idm:picketlink-idm-core"))
//      .addLibraries(MavenArtifactResolver.resolve("org.picketlink.idm:picketlink-idm-spi"))
//      .addLibraries(MavenArtifactResolver.resolve("org.jboss.seam.persistence:seam-persistence"))

                .addLibraries(MavenArtifactResolver.resolve("org.openid4java:openid4java-server"))
                .addLibraries(MavenArtifactResolver.resolve("org.openid4java:openid4java-nodeps"))
                .addLibraries(MavenArtifactResolver.resolve("commons-httpclient:commons-httpclient"))
                .addLibraries(MavenArtifactResolver.resolve("nekohtml:nekohtml"))
                .addLibraries(MavenArtifactResolver.resolve("org.jboss.seam.servlet:seam-servlet"))
                .addLibraries(MavenArtifactResolver.resolve("org.jboss.seam.solder:seam-solder"))
                .setWebXML("WEB-INF/web.xml");
        ;
        return archive;
    }


    @Inject
    OpenIdTestClient openIdTestClient;

    @Test
    public void testDeploy() throws Exception {

        String destinationUrl = openIdTestClient.authRequest("http://localhost:8080/test/users/test_user");
        Assert.assertNotNull(destinationUrl);
        HttpClient client = new HttpClient();
        HttpMethod method = new GetMethod(destinationUrl);
        int statusCode = client.executeMethod(method);
        Assert.assertEquals(200, statusCode);

        //Rest of test should be done in jsfunit or similar framework.

        //Form processing is: Login.jsf , login -> Attributes.jsf, ok -> back to relaying party


    }


}
