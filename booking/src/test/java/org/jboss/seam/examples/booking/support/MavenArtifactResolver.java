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
package org.jboss.seam.examples.booking.support;

import java.io.File;

/**
 * A temporary resolver that converts a Maven artifact reference into a {@link java.io.File} object.
 * 
 * <p>
 * This approach is an interim solution for Maven projects until the open feature request to add formally add artifacts to a
 * test (<a href="https://jira.jboss.org/browse/ARQ-66">ARQ-66</a>) is implementated.
 * </p>
 * 
 * <p>
 * The testCompile goal will resolve any test dependencies and put them in your local Maven repository. By the time the test
 * executes, you can be sure that the JAR files you need will be in your local repository.
 * </p>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * WebArchive war = ShrinkWrap.create(&quot;test.war&quot;, WebArchive.class).addLibrary(
 *         MavenArtifactResolver.resolve(&quot;commons-lang:commons-lang:2.5&quot;));
 * </pre>
 * 
 * <p>
 * If you are using an alternate local Maven repository, you need to pass it to the Maven surefire plugin using the following
 * stanza in the plugin configuration element:
 * </p>
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
 * <p>
 * Another approach to pull in a library is to add packages recursively from the root library package.
 * </p>
 */
public class MavenArtifactResolver {
    private static final String LOCAL_MAVEN_REPO = System.getProperty("maven.repo.local") != null ? System
            .getProperty("maven.repo.local") : (System.getProperty("user.home") + File.separatorChar + ".m2"
            + File.separatorChar + "repository");

    public static File resolve(String groupId, String artifactId, String version) {
        return new File(LOCAL_MAVEN_REPO + File.separatorChar + groupId.replace(".", File.separator) + File.separatorChar
                + artifactId + File.separatorChar + version + File.separatorChar + artifactId + "-" + version + ".jar");
    }

    public static File resolve(String qualifiedArtifactId) {
        String[] segments = qualifiedArtifactId.split(":");
        return resolve(segments[0], segments[1], segments[2]);
    }
}
