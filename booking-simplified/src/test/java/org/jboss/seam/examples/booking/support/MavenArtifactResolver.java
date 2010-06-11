package org.jboss.seam.examples.booking.support;

import java.io.File;

public class MavenArtifactResolver
{
   private static final String LOCAL_MAVEN_REPO =
         System.getProperty("user.home") + File.separatorChar +
         ".m2" + File.separatorChar + "repository";

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
