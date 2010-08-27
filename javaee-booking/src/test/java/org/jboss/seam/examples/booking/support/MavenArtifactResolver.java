package org.jboss.seam.examples.booking.support;

import java.io.File;

/**
 * A temporary resolver that converts a Maven artifact reference
 * into a {@link java.io.File} object.
 * 
 * <p>This approach is an interim solution for Maven projects
 * until the open feature request to add formally add artifacts
 * to a test (<a href="https://jira.jboss.org/browse/ARQ-66">ARQ-66</a>) is implementated.</p>
 *
 * <p>The testCompile goal will resolve any test dependencies and
 * put them in your local Maven repository. By the time the test
 * executes, you can be sure that the JAR files you need will be
 * in your local repository.</p>
 *
 * <p>Example usage:</p>
 * 
 * <pre>
 * WebArchive war = ShrinkWrap.create("test.war", WebArchive.class)
 *     .addLibrary(MavenArtifactResolver.resolve("commons-lang:commons-lang:2.5"));
 * </pre>

 * <p>If you are using an alternate local Maven repository, you need to pass it
 * to the Maven surefire plugin using the following stanza in the plugin
 * configuration element:</p>
 *
 * <pre>
 * &lt;systemProperties&gt;
 *    &lt;property&gt;
 *       &lt;name&gt;maven.repo.local&lt;/name&gt;
 *       &lt;value&gt;${maven.repo.local}&lt;/value&gt;
 *    &lt;/property&gt;
 * &lt;/systemProperties&gt;
 * </pre>
 *
 * <p>Another approach to pull in a library is to add packages recursively from the
 * root library package.</p>
 */
public class MavenArtifactResolver
{
   private static final String LOCAL_MAVEN_REPO =
         System.getProperty("maven.repo.local") != null ? System.getProperty("maven.repo.local") :
               (System.getProperty("user.home") + File.separatorChar +
               ".m2" + File.separatorChar + "repository");

   public static File resolve(String groupId, String artifactId, String version)
   {
      return new File(LOCAL_MAVEN_REPO + File.separatorChar +
            groupId.replace(".", File.separator) + File.separatorChar +
            artifactId + File.separatorChar +
            version + File.separatorChar +
            artifactId + "-" + version + ".jar");
   }

   public static File resolve(String qualifiedArtifactId)
   {
      String[] segments = qualifiedArtifactId.split(":");
      return resolve(segments[0], segments[1], segments[2]);
   }
}
